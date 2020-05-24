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

package org.meshr.converter.transform;

//import io.vertx.core.AbstractVerticle;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.core.Promise;
//import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceBinder;
//import io.vertx.config.ConfigRetriever;
import io.vertx.core.json.JsonObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
//import java.util.logging.Logger;
import java.util.Properties;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.reactivex.Single;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.core.Future;
import io.vertx.core.Context;



public class TransformVerticle extends AbstractVerticle {

    public static final String CONFIG_TRANSFORM_QUEUE = "transform.queue";
    private static final Logger LOG = LoggerFactory.getLogger(TransformVerticle.class);
    HashMap<String, String> tokenCache = new HashMap<String,String>();

    @Override
    public void start() throws Exception {

      TransformService transformService = TransformService.create(vertx, config(), tokenCache);

      new ServiceBinder(vertx.getDelegate())
          .setAddress(CONFIG_TRANSFORM_QUEUE)
          .register(TransformService.class, transformService);
      LOG.info("TransformService registereded.");
    }
}