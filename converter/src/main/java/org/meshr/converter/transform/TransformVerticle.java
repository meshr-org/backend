package org.meshr.converter.transform;

/*
 * Copyright (c) 2020 Robert Sahlin
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE file.
 */

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
import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.ProjectTopicName;
import java.util.concurrent.TimeUnit;
import com.google.cloud.ServiceOptions;
import io.vertx.ext.web.client.WebClient;
import io.vertx.core.http.RequestOptions;


public class TransformVerticle extends AbstractVerticle {

    public static final String CONFIG_TRANSFORM_QUEUE = "transform.queue";
    private static final String PROJECT_ID = ServiceOptions.getDefaultProjectId();
    private WebClient client;
    private static final Logger LOG = LoggerFactory.getLogger(TransformVerticle.class);

    @Override
    //public void start(Promise<Void> promise) throws Exception {
    public void start() throws Exception {
        
        TransformService transformService = TransformService.create();
    
        new ServiceBinder(vertx.getDelegate())
            .setAddress(CONFIG_TRANSFORM_QUEUE)
            .register(TransformService.class, transformService);
        LOG.info("TransformService registereded.");
    }
}