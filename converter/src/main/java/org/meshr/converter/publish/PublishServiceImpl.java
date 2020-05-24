/*
 * Copyright (c) 2020 Robert Sahlin
 *
 * Use of this software is governed by the Business Source License 1.1.
 * 
 * Parameters
 * 
 * Licensor:             Robert Sahlin
 * Licensed Work:        Meshr
 *                       The Licensed Work is (c) 2020 Robert Sahlin.
 * Additional Use Grant: You may use the Licensed Work when the Licensed Work is 
 *                       processing less than 10 Million unique events per month, 
 *                       provided that you do not use the Licensed Work for a 
 *                       commercial offering that allows third parties to access
 *                       the functionality of the Licensed Work so that such third
 *                       parties directly benefit from the features of the Licensed Work.
 * 
 * Change Date:          12 months after the git commit date of the code
 * 
 * Change License:       GNU AFFERO GENERAL PUBLIC LICENSE, Version 3
 * 
 * For information about alternative licensing arrangements for the Licensed Work,
 * please contact the licensor.
 */

package org.meshr.converter.publish;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.CompositeFuture;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.Vertx;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import com.google.cloud.ServiceOptions;

import com.google.common.cache.LoadingCache;
import com.google.cloud.pubsub.v1.Publisher;

import  java.util.Base64;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;
import java.util.UUID;
import java.util.Optional;
import com.google.common.collect.ImmutableMap;
import java.time.Instant;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.StorageOptions;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;

import java.io.InputStream;
import java.nio.channels.Channels;
import java.io.IOException;

//import org.datahem.protobuf.measurementprotocol.v2.*;
//import com.google.pubsub.v1.ProjectTopicName;
//import com.google.pubsub.v1.PubsubMessage;

//import org.meshr.processor.measurementprotocol.v2.utils.*;
/*
import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.bigquery.model.TableSchema;
import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TimePartitioning;
import com.google.api.services.bigquery.model.TableDataInsertAllRequest;
import com.google.api.services.bigquery.model.TableDataInsertAllResponse;
import com.google.api.services.bigquery.BigqueryScopes;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.bigquery.model.ErrorProto;
import com.google.api.services.bigquery.Bigquery.Tables;
import com.google.api.services.bigquery.model.Table;
import com.google.api.services.bigquery.model.TableSchema;
import com.google.api.services.bigquery.model.TableReference;
import com.google.api.services.bigquery.model.TimePartitioning;
import com.google.api.services.bigquery.model.Clustering;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import tech.allegro.schema.json2avro.converter.AvroConversionException;
import tech.allegro.schema.json2avro.converter.JsonAvroConverter;
*/

//import org.apache.beam.sdk.io.gcp.bigquery.BigQueryAvroUtils;
//import org.meshr.processor.utils.ProtobufUtils;


class PublishServiceImpl implements PublishService {

    private static final Logger LOG = LoggerFactory.getLogger(PublishServiceImpl.class);
    LoadingCache<String, Publisher> publisherCache;

    PublishServiceImpl(LoadingCache<String, Publisher> publisherCache) {
        LOG.info("Publish service ...");    
        this.publisherCache = publisherCache;
    }

    @Override
    public PublishService publish(
        JsonObject body, 
        String namespace, 
        String name, 
        Handler<AsyncResult<JsonObject>> resultHandler) {
            try {
                Map<String,String> attributes = body.getJsonObject("attributes")
                    .getMap()
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getValue() instanceof String)
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> (String)e.getValue()));
                
                PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                    .putAllAttributes(ImmutableMap.<String, String>builder()
                        .putAll(attributes)
                        .build())
                    .setData(ByteString.copyFromUtf8(body.getString("data")))
                    .build();
                
                ApiFuture<String> topicFuture = publisherCache.get(namespace + "." + name).publish(pubsubMessage);
                ApiFutures.addCallback(topicFuture,
                new ApiFutureCallback<String>() {
                    public void onSuccess(String messageId) {
                        resultHandler.handle(Future.succeededFuture(body));
                    }
                    public void onFailure(Throwable t) {
                        LOG.error("Failed to publish: ", t);
                        resultHandler.handle(Future.failedFuture(t));    
                    }
                }, MoreExecutors.directExecutor());
            } catch (Exception e) {
                LOG.error("PubSubClient contextInitialized error ", e);
                resultHandler.handle(Future.failedFuture(e));
            }
            return this;
    }
}