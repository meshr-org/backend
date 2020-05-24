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

public class PublishVerticle extends AbstractVerticle {

    public static final String CONFIG_PUBLISH_QUEUE = "publish.queue";
    private static final String PROJECT_ID = ServiceOptions.getDefaultProjectId();
    private static LoadingCache<String, Publisher> publishers;
    private static CacheLoader<String, Publisher> loader;
    private static RemovalListener<String, Publisher> removalListener;
    //private WebClient client;
    private static final Logger LOG = LoggerFactory.getLogger(PublishVerticle.class);

    @Override
    public void start() throws Exception {
        
        loader = new CacheLoader<String, Publisher>() {
            @Override
            public Publisher load(String pubSubTopicId) {
                Publisher publisher = null;
                try{
                    //LOG.info("config: " + config());
                    ProjectTopicName topic = ProjectTopicName.of(config().getString("PROJECT_ID", ServiceOptions.getDefaultProjectId()), pubSubTopicId);
                    publisher = Publisher
                        .newBuilder(topic)
                        .build();
                    LOG.info("Cache load: " + publisher.getTopicNameString() + ", ref: " + publisher.toString());
                }catch (Exception e) {
                    LOG.info("PubSubClient Connect load error " + e.toString());
                }
                return publisher;
            }
		};
			
        removalListener = new RemovalListener<String, Publisher>() {
            @Override
            public void onRemoval(RemovalNotification<String, Publisher> removal) {
                final Publisher publisher = removal.getValue();
                LOG.info("Cache remove: " + publisher.getTopicNameString() + ", ref: " + publisher.toString());
                if (publisher != null) {
                    publisher.shutdown();
                    vertx.executeBlocking(promise -> {
                        try{
                            publisher.awaitTermination(1, TimeUnit.SECONDS);
                        }catch(Exception e){
                            LOG.error("PubSubClient Connect load error " + e);
                        }    
                        promise.complete("");
                    }, 
                    res -> {
                        LOG.info("Publisher terminated");
                    });
                }
            }
        };
			
        publishers = CacheBuilder
            .newBuilder()
            .maximumSize(1000)
            .removalListener(removalListener)
            .expireAfterAccess(60, TimeUnit.SECONDS)
            .build(loader);
            
        PublishService publishService = PublishService.create(publishers);
    
        new ServiceBinder(vertx.getDelegate())
            .setAddress(CONFIG_PUBLISH_QUEUE)
            .register(PublishService.class, publishService);
        LOG.info("PublishService registereded.");
    }
}