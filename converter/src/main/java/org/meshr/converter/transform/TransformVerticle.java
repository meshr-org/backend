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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executor;

//import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
//import com.github.benmanes.caffeine.cache.Caffeine;
/*
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
*/

public class TransformVerticle extends AbstractVerticle {

    public static final String CONFIG_TRANSFORM_QUEUE = "transform.queue";
    private static final Logger LOG = LoggerFactory.getLogger(TransformVerticle.class);
    //private static LoadingCache<String, String> tokens;
    //private static CacheLoader<String, String> loader;
    //private static RemovalListener<String, Single<String>> removalListener;
    WebClient client = WebClient.create(vertx);
    AsyncLoadingCache<String, String> tokenCache;

    @Override
    public void start() throws Exception {

        Context context = vertx.getOrCreateContext();
        contextExecutor = cmd -> context.runOnContext(v -> cmd.run());

        tokenCache = Caffeine.newBuilder()
            .executor(contextExecutor)
            .buildAsync((key, executor) -> toCompletableFuture(loadToken(key)));

            TransformService transformService = TransformService.create(vertx, config(), tokenCache);
    
            new ServiceBinder(vertx.getDelegate())
                .setAddress(CONFIG_TRANSFORM_QUEUE)
                .register(TransformService.class, transformService);
            LOG.info("TransformService registereded.");
        }
        
        private Future<String> loadToken(String serviceUrl) {
          Promise<HttpResponse<JsonObject>> promise = Promise.promise();

          String tokenUrl = String.format("http://metadata/computeMetadata/v1/instance/service-accounts/default/identity?audience=%s", serviceUrl);
          client
              .get(tokenUrl)
              //.as(BodyCodec.jsonObject())
              //.expect(ResponsePredicate.SC_OK)
              .ssl(true)
              .timeout(10000)
              .putHeader("Metadata-Flavor", "Google")
              .send(promise);
      
          return promise.future().map(HttpResponse::bodyAsString);
        }

      }

        /*    
        loader = new CacheLoader<String, String>() {
            @Override
            public String load(String serviceUrl) {
                String tokenUrl = String.format("http://metadata/computeMetadata/v1/instance/service-accounts/default/identity?audience=%s", serviceUrl);
                client
                    .get(tokenUrl)
                    .ssl(true)
                    .timeout(10000)
                    .putHeader("Metadata-Flavor", "Google")
                    .rxSend()
                    .map(HttpResponse::bodyAsString)
                    .subscribe(
                        resp -> {
                            return resp;
                        }
                    );
            }
		};
        

			
        tokens = CacheBuilder
            .newBuilder()
            .maximumSize(1000)
            //.removalListener(removalListener)
            .expireAfterAccess(60, TimeUnit.SECONDS)
            .build(loader);
        
    private Future<JsonObject> loadToken(String serviceUrl) {
        Promise<JsonObject> promise = Promise.promise();
    
        AsyncLoadingCache<Integer, JsonObject> authorCache = getTokenCache(env);
        authorCache.get(authorId).whenComplete((jsonObject, throwable) -> {
          if (throwable == null) {
            promise.complete(jsonObject);
          } else {
            promise.fail(throwable);
          }
        });
    
        return promise.future();
    }*/