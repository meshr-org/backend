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

package org.meshr.loader.bigquery;

import io.vertx.core.AbstractVerticle;
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


public class BigQueryVerticle extends AbstractVerticle {

    public static final String CONFIG_BIGQUERY_QUEUE = "bigquery.queue";
    private static final String PROJECT_ID = ServiceOptions.getDefaultProjectId();
    private WebClient client;
    private static final Logger LOG = LoggerFactory.getLogger(BigQueryVerticle.class);

  @Override
  public void start(Promise<Void> promise) throws Exception {
            
            BigQueryService.create(
                            config().put("PROJECT_ID", PROJECT_ID),
                            WebClient.create(vertx),
                            ready -> {
                                if (ready.succeeded()) {
                                    LOG.info("BigQueryService created.");
                                    ServiceBinder binder = new ServiceBinder(vertx);
                                    binder
                                        .setAddress(CONFIG_BIGQUERY_QUEUE)
                                        .register(BigQueryService.class, ready.result());
                                        LOG.info("BigQueryService registereded.");
                                    promise.complete();
                                } else {
                                    LOG.error("BigQueryService failed to create.");
                                    promise.fail(ready.cause());
                                }
                            }
                        );
    }
}