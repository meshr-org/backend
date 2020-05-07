package org.meshr.converter.http;

/*
 * Copyright (c) 2020 Robert Sahlin
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE file.
 */

import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.json.JsonObject;


import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Base64;


import org.meshr.converter.transform.TransformService;

import com.google.common.collect.Multimap;
import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.stream.Stream;
import java.util.Enumeration;
import java.util.UUID;
import com.google.common.collect.ImmutableMap;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class HttpServerVerticle extends AbstractVerticle {

    private final static Logger LOG = LoggerFactory.getLogger("HttpServerVerticle.class");

    public static final String CONFIG_HTTP_SERVER_PORT = "http.server.port";
    public static final String CONFIG_TRANSFORM_QUEUE = "transform.queue";
    private TransformService transformService;
    
    @Override
    public void start(Promise<Void> promise) throws Exception {

        String transformQueue = config().getString(CONFIG_TRANSFORM_QUEUE, "transform.queue");
        transformService = TransformService.createProxy(vertx, transformQueue);
        
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

        transformService.transform(body, topic, handler);
    }
}