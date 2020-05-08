package org.meshr.converter;

/*
 * Copyright (c) 2020 Robert Sahlin
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE file.
 */

//import io.vertx.core.AbstractVerticle;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.Promise;
import io.reactivex.Single;
import io.reactivex.Observable;
//import io.vertx.config.ConfigRetriever;
import io.vertx.reactivex.config.ConfigRetriever;

import org.meshr.converter.http.HttpServerVerticle;
import org.meshr.converter.transform.TransformVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class MainVerticle extends AbstractVerticle {

    private final static Logger LOG = LoggerFactory.getLogger("MainVerticle.class");

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MainVerticle());
    }

    @Override
    //public void start(Promise<Void> promise) throws Exception { 
        public void start(Future<Void> future) throws Exception { 
        LOG.info("MainVerticle deployed");

        ConfigRetriever retriever = ConfigRetriever.create(vertx);
        Observable<String> verticleNames = Observable.just(
            HttpServerVerticle.class.getName(),
            TransformVerticle.class.getName()
        );
        
        retriever.rxGetConfig()
            .flatMap(config -> verticleNames
                    .flatMapSingle(verticleName -> vertx.rxDeployVerticle(verticleName, new DeploymentOptions().setConfig(config)))
                    .toList()
            )
            .subscribe((rec, err) -> {
                if (rec != null) {
                  future.complete();
                } else {
                  future.fail(err);
                }
              });
        


        /*
        ConfigRetriever retriever = ConfigRetriever.create(vertx);
            retriever.getConfig(
                config -> {
                    if (config.failed()) {
                        LOG.info("Config retriever failed.");
                        promise.fail(config.cause());
                    } else {
                        CompositeFuture.all(
                            deployHelper(HttpServerVerticle.class.getName(), config.result()),
                            deployHelper(TransformVerticle.class.getName(), config.result()))
                        .setHandler(result -> { 
                            if(result.succeeded()){
                                promise.complete();
                            } else {
                                promise.fail(result.cause());
                            }
                        });
                    }
            });*/

    }

    //private Future<Void> deployHelper(String name, JsonObject config){
        private Single<String> deployHelper(String name, JsonObject config){
        //final Future<Void> future = Future.future();
        return vertx.rxDeployVerticle(name, new DeploymentOptions().setConfig(config));
            /*res -> {
                if(res.failed()){
                    LOG.error("Failed to deploy verticle " + name);
                    future.fail(res.cause());
                } else {
                    LOG.info("Deployed verticle " + name);
                    future.complete();
                }
            }
        );
        return future;*/
    }
}