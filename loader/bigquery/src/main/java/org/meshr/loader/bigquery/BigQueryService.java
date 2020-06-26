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

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.core.http.RequestOptions;

//import com.google.common.cache.LoadingCache;
//import com.google.cloud.pubsub.v1.Publisher;

import java.util.Map;
import java.util.HashMap;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;


@ProxyGen
@VertxGen
public interface BigQueryService {

    @Fluent
    BigQueryService insertData(
        JsonObject body, 
        String topic, 
        Handler<AsyncResult<Void>> resultHandler
    );

    @GenIgnore
    static BigQueryService create(
        JsonObject config,
        WebClient client,
        Handler<AsyncResult<BigQueryService>> readyHandler) {
            return new BigQueryServiceImpl( 
                config, 
                client, 
                readyHandler);
        }

    @GenIgnore
    static BigQueryService createProxy(Vertx vertx, String address) {
        return new BigQueryServiceVertxEBProxy(vertx, address);
    }
}