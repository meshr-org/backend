package org.meshr.converter.publish;

/*
 * Copyright (c) 2020 Robert Sahlin
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE file.
 */

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
//import io.vertx.ext.web.client.WebClient;
//import io.vertx.core.http.RequestOptions;

import io.vertx.reactivex.core.AbstractVerticle;

import com.google.common.cache.LoadingCache;
import com.google.cloud.pubsub.v1.Publisher;

//import java.util.Map;
//import java.util.HashMap;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;


@ProxyGen
@VertxGen
public interface PublishService {

    @Fluent
    PublishService publish(
        JsonObject body, 
        String namespace,
        String name,
        Handler<AsyncResult<JsonObject>> resultHandler);

    @GenIgnore
    static PublishService create(LoadingCache<String, Publisher> publisherCache) { return new PublishServiceImpl(publisherCache);}

    @GenIgnore
    static org.meshr.converter.publish.reactivex.PublishService createProxy(Vertx vertx, String address) {
        return new org.meshr.converter.publish.reactivex.PublishService(new PublishServiceVertxEBProxy(vertx, address));
    }
}