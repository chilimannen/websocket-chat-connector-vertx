package com.websocket.chat.connector;

import io.vertx.core.*;

/**
 * Created by Robin on 2015-12-19.
 */
public class Launcher implements Verticle {
    private Vertx vertx;

    @Override
    public Vertx getVertx() {
        return vertx;
    }

    @Override
    public void init(Vertx vertx, Context context) {
        this.vertx = vertx;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertx.deployVerticle(new DatabaseConnector());
        vertx.deployVerticle(new ChatConnector());
        vertx.deployVerticle(new RegistryConnector());
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        vertx.close();
    }
}
