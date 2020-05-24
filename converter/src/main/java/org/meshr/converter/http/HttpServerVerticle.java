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

package org.meshr.converter.http;

import io.vertx.core.*;
//import io.vertx.reactivex.core.*;
import io.vertx.core.buffer.Buffer;
//import io.vertx.core.http.HttpServer;
import io.vertx.reactivex.core.http.HttpServer;
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
import org.meshr.converter.encode.EncodeService;
import org.meshr.converter.publish.PublishService;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.reactivex.Single;
import io.reactivex.Observable;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class HttpServerVerticle extends AbstractVerticle {

    private final static Logger LOG = LoggerFactory.getLogger("HttpServerVerticle.class");
    public static final String CONFIG_TRANSFORM_QUEUE = "transform.queue";
    private org.meshr.converter.transform.reactivex.TransformService transformService;
    public static final String CONFIG_ENCODE_QUEUE = "encode.queue";
    private org.meshr.converter.encode.reactivex.EncodeService encodeService;
    public static final String CONFIG_PUBLISH_QUEUE = "publish.queue";
    private org.meshr.converter.publish.reactivex.PublishService publishService;
    
    //@Override
    public void start(Promise<Void> promise) throws Exception {

        String transformQueue = config().getString(CONFIG_TRANSFORM_QUEUE, "transform.queue");
        transformService = TransformService.createProxy(vertx, transformQueue);
        String encodeQueue = config().getString(CONFIG_ENCODE_QUEUE, "encode.queue");
        encodeService = EncodeService.createProxy(vertx.getDelegate(), encodeQueue);
        String publishQueue = config().getString(CONFIG_PUBLISH_QUEUE, "publish.queue");
        publishService = PublishService.createProxy(vertx.getDelegate(), publishQueue);
        
        Router apiRouter = Router.router(vertx);
        apiRouter.route();

        apiRouter
            .post()
            .handler(BodyHandler.create());
        
        apiRouter
            .post("/source/namespace/:namespace/name/:name")
            .handler(this::sourceConverter);

        vertx
            .createHttpServer()
            .requestHandler(apiRouter)
            .rxListen(config().getInteger("HTTP_PORT", 8080))
            .subscribe(
                server -> {
                    LOG.info("HTTP server running");
                    promise.complete();
                },
                failure -> {
                    LOG.error("Could not start a HTTP server");
                    promise.fail("Could not start a HTTP server");
                }
            );
    }

    private void sourceConverter(RoutingContext context) {
        String namespace = String.valueOf(context.request().getParam("namespace"));
        String name = String.valueOf(context.request().getParam("name"));       
        JsonObject body = context.getBodyAsJson();        
        //LOG.info("apiReceiver body: " + body.toString());

        transformService.rxTransform(body, namespace, name)
        .toObservable()
        /*.flatMapSingle(transformedBody -> {
            LOG.info(transformedBody);
            return Single.just(transformedBody);
        })*/
        .flatMapSingle(transformedBody -> encodeService.rxEncode(transformedBody,namespace, name))
        .flatMapSingle(encodedBody -> publishService.rxPublish(encodedBody, namespace, name))
        .subscribe(
            jsonObject -> {
                System.out.println(jsonObject.encodePrettily());
                context.response().setStatusCode(200).end();
            },
            throwable -> {
                System.out.println(throwable.getMessage());
                context.fail(500);
            }
        );

    }
}