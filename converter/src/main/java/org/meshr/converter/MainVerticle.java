package org.meshr.converter;

/*
 * Copyright (c) 2020 Robert Sahlin
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE file.
 */

import io.vertx.core.Vertx;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.Promise;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.config.ConfigRetriever;

import io.reactivex.Single;
import io.reactivex.Observable;

import org.meshr.converter.http.HttpServerVerticle;
import org.meshr.converter.transform.TransformVerticle;

public class MainVerticle extends AbstractVerticle {

    private final static Logger LOG = LoggerFactory.getLogger("MainVerticle.class");

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MainVerticle());
    }

    @Override
    public void start(Future<Void> future) throws Exception { 
        LOG.info("MainVerticle deployed");

        ConfigRetriever retriever = ConfigRetriever.create(vertx);
        Observable<String> verticleNames = Observable.just(
            HttpServerVerticle.class.getName(),
            TransformVerticle.class.getName()
            //EncodeVerticle.class.getName(),
            //DecodeVerticle.class.getName(),
            //PublishVerticle.class.getName()
        );
        
        retriever.rxGetConfig()
            .flatMap(config -> verticleNames
                .flatMapSingle(verticleName -> vertx.rxDeployVerticle(verticleName, new DeploymentOptions().setConfig(config)))
                .toList())
            .subscribe((rec, err) -> {
                if (rec != null) {future.complete();} 
                else {future.fail(err);}});
    }
}