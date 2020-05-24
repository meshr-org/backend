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

package org.meshr.converter;

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
import org.meshr.converter.encode.EncodeVerticle;
import org.meshr.converter.publish.PublishVerticle;

import com.google.cloud.ServiceOptions;

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
            TransformVerticle.class.getName(),
            EncodeVerticle.class.getName(),
            //DecodeVerticle.class.getName(),
            PublishVerticle.class.getName()
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