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

package org.meshr.converter.encode;

//import io.vertx.core.AbstractVerticle;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.core.Promise;
//import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.json.JsonObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
//import java.util.logging.Logger;
import java.util.Properties;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import java.util.concurrent.TimeUnit;
import com.google.cloud.ServiceOptions;
import io.vertx.ext.web.client.WebClient;
import io.vertx.core.http.RequestOptions;
import org.apache.avro.Schema;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.StorageOptions;
import java.io.InputStream;
import java.nio.channels.Channels;

public class EncodeVerticle extends AbstractVerticle {

    public static final String CONFIG_TRANSFORM_QUEUE = "encode.queue";
    private static final String PROJECT_ID = ServiceOptions.getDefaultProjectId();
    private static final Logger LOG = LoggerFactory.getLogger(EncodeVerticle.class);
    private static CacheLoader<String, Schema> loader;
    private static LoadingCache<String, Schema> schemas;

    @Override
    public void start() throws Exception {

        String bucketName = "datahem-schemas"; //set with environment variables

        loader = new CacheLoader<String, Schema>() {
            @Override
            public Schema load(String fullName) {
                Schema schema = null;

                try{
                    LOG.info(bucketName);
                    LOG.info(fullName);
                    Storage storage = StorageOptions.getDefaultInstance().getService();
                    Blob blob = storage.get(BlobId.of(bucketName, fullName + ".avsc"));
                    ReadChannel reader = blob.reader();
                    InputStream inputStream = Channels.newInputStream(reader);
                    schema = new Schema.Parser().parse(inputStream);
                    return schema;
                }catch (Exception e){
                    LOG.info("cloud storage error");
                    e.printStackTrace();
                    return null;
                }
            }
		};
			
        schemas = CacheBuilder
            .newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(600, TimeUnit.SECONDS)
            .build(loader);
        
        EncodeService encodeService = EncodeService.create(schemas);
    
        new ServiceBinder(vertx.getDelegate())
            .setAddress(CONFIG_TRANSFORM_QUEUE)
            .register(EncodeService.class, encodeService);
        LOG.info("EncodeService registereded.");
    }
}