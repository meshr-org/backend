package org.meshr.loader;

/*
 * Copyright (c) 2020 Robert Sahlin
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE file.
 */

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.config.ConfigRetriever;
import org.meshr.loader.bigquery.BigQueryVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

    private final static Logger LOG = LoggerFactory.getLogger("MainVerticle.class");

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MainVerticle());
    }

    @Override
    public void start(Promise<Void> promise) throws Exception { 
        LOG.info("MainVerticle deployed");

        ConfigRetriever retriever = ConfigRetriever.create(vertx);
            retriever.getConfig(
                config -> {
                    if (config.failed()) {
                        LOG.info("Config retriever failed.");
                        promise.fail(config.cause());
                    } else {
                        Promise<String> bigQueryVerticleDeployment = Promise.promise();
                        vertx.deployVerticle(
                            BigQueryVerticle.class.getName(),
                            new DeploymentOptions()
                                .setConfig(config.result()), 
                            bigQueryVerticleDeployment
                        );
                        bigQueryVerticleDeployment.future().compose(id -> {
                            Promise<String> httpVerticleDeployment = Promise.promise();
                            vertx.deployVerticle(
                                "org.meshr.processor.vertx.http.HttpServerVerticle",
                                new DeploymentOptions()
                                    .setInstances(1)
                                    .setConfig(config.result()),
                                httpVerticleDeployment
                            );
                            return httpVerticleDeployment.future();
                        }).setHandler(ar -> {
                            if (ar.succeeded()) {
                                LOG.info("BigQueryVerticle deployed");
                                promise.complete();
                            } else {
                                LOG.error("BigQueryVerticle failed to deploy");
                                promise.fail(ar.cause());
                            }
                        });
                    }
            });
    }
}