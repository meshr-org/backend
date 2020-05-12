package org.meshr.converter.encode;

/*
 * Copyright (c) 2020 Robert Sahlin
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE file.
 */

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
import java.util.StringJoiner;
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

import java.io.InputStream;
import java.nio.channels.Channels;
import java.io.IOException;

//import org.datahem.protobuf.measurementprotocol.v2.*;
//import com.google.pubsub.v1.ProjectTopicName;
//import com.google.pubsub.v1.PubsubMessage;

//import org.meshr.processor.measurementprotocol.v2.utils.*;

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


//import org.apache.beam.sdk.io.gcp.bigquery.BigQueryAvroUtils;
//import org.meshr.processor.utils.ProtobufUtils;


class EncodeServiceImpl implements EncodeService {

  private static final Logger LOG = LoggerFactory.getLogger(EncodeServiceImpl.class);

    //LoadingCache<String, Publisher> publisherCache;

    EncodeServiceImpl() {
        LOG.info("Encode service ...");    
    }

    @Override
    public EncodeService encode(
        JsonObject body, 
        String namespace, 
        String name, 
        Handler<AsyncResult<JsonObject>> resultHandler) {
            LOG.info("Encoding...");        
            try{
                LOG.info("Trying...");        
                String bucketName = "datahem-schemas";
                StringJoiner fileName = new StringJoiner(".");
                fileName.add(namespace).add(name).add("avsc");
                //"com.google.analytics.v2.Event.avsc";
                Schema schema = Schema.create(Schema.Type.STRING);
                schema = getAvroSchemaFromCloudStorage(bucketName, fileName.toString());
                LOG.info(schema.toString());
                JsonAvroConverter converter = new JsonAvroConverter();
                //GenericData.Record record = converter.convertToGenericDataRecord(body.getJsonObject("data").toString().getBytes(), schema);
                byte[] binaryAvro = converter.convertToAvro(body.getJsonObject("data").toString().getBytes(), schema);
                body.put("data", Base64.getEncoder().encodeToString(binaryAvro));
                body.getJsonObject("attributes").put("namespace", namespace).put("name", name).put("format", "avro/binary");
                resultHandler.handle(Future.succeededFuture(body));
            }catch(Exception e){
                resultHandler.handle(Future.failedFuture(e));
            }
            return this;
    }

    public static Schema getAvroSchemaFromCloudStorage(String bucketName, String fileName) throws Exception {
        try{
            LOG.info(bucketName);
            LOG.info(fileName);
            Storage storage = StorageOptions.getDefaultInstance().getService();
            Blob blob = storage.get(BlobId.of(bucketName, fileName));
            ReadChannel reader = blob.reader();
            InputStream inputStream = Channels.newInputStream(reader);
            Schema schema = new Schema.Parser().parse(inputStream);
            return schema;
        }catch (Exception e){
            LOG.info("cloud storage error");
            e.printStackTrace();
            return null;
        }
    }
}