package org.meshr.converter.encode;

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

//import com.google.common.cache.LoadingCache;
//import com.google.cloud.pubsub.v1.Publisher;

//import java.util.Map;
//import java.util.HashMap;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;


@ProxyGen
@VertxGen
public interface EncodeService {

    @Fluent
    EncodeService encode(
        JsonObject body, 
        String namespace,
        String name,
        Handler<AsyncResult<JsonObject>> resultHandler);

    @GenIgnore
    static EncodeService create() { return new EncodeServiceImpl();}

    @GenIgnore
    static org.meshr.converter.encode.reactivex.EncodeService createProxy(Vertx vertx, String address) {
        return new org.meshr.converter.encode.reactivex.EncodeService(new EncodeServiceVertxEBProxy(vertx, address));
    }
}