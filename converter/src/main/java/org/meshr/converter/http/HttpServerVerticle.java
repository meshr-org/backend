package org.meshr.converter.http;

/*
 * Copyright (c) 2020 Robert Sahlin
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE file.
 */

import io.vertx.core.*;
//import io.vertx.reactivex.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
//import io.vertx.ext.web.Router;
//import io.vertx.ext.web.RoutingContext;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.codec.BodyCodec;
//import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.config.ConfigRetriever;


import org.meshr.converter.transform.TransformService;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.reactivex.Single;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class HttpServerVerticle extends AbstractVerticle {

    private final static Logger LOG = LoggerFactory.getLogger("HttpServerVerticle.class");
    public static final String CONFIG_TRANSFORM_QUEUE = "transform.queue";
    private org.meshr.converter.transform.reactivex.TransformService transformService;
    
    //@Override
    public void start(Promise<Void> promise) throws Exception {

        String transformQueue = config().getString(CONFIG_TRANSFORM_QUEUE, "transform.queue");
        transformService = TransformService.createProxy(vertx.getDelegate(), transformQueue);
        
        Router apiRouter = Router.router(vertx);
        apiRouter.route();

        apiRouter
            .post()
            .handler(BodyHandler.create());
        
        apiRouter
            .post("/topic/:id")
            .handler(this::apiReceiver);

        vertx
            .createHttpServer()
            .requestHandler(apiRouter)
            .listen(config().getInteger("HTTP_PORT", 8080),
                ar -> {
                    if (ar.succeeded()) {
                        LOG.info("HTTP server running on port: " + ar.result().actualPort());
                        promise.complete();
                    } else {
                        LOG.error("Could not start a HTTP server: " + ar.cause().toString());
                        promise.fail(ar.cause());
                    }
                }
            );
    }

    private void apiReceiver(RoutingContext context) {
        String topic = String.valueOf(context.request().getParam("id"));
        
        JsonObject body = context.getBodyAsJson();        
        LOG.info("apiReceiver body: " + body.toString());
        
        Handler<AsyncResult<Void>> handler = reply -> {
            if (reply.succeeded()) {
                LOG.info("apiPost handler reply succeded: ", reply);
                context.response().setStatusCode(204).end();
            } else {
                LOG.error("apiPost handler reply fail: ", reply.cause());
                context.fail(reply.cause());
            }
        };

        Single<JsonObject> single = transformService.rxTransform(body, topic);
        single.subscribe(
            jsonObject -> System.out.println(jsonObject.encodePrettily()),
            throwable -> System.out.println(throwable.getMessage())
        );
    }
}