package com.websocket.chat.connector;

import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;

/**
 * Created by Robin on 2015-12-19.
 * <p>
 * unregister/registerChatServer and server up/down/full/ready
 */
class RegistryConnector implements Verticle {
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
        vertx.createHttpClient().websocket(
                Configuration.REGISTRY_PORT, "localhost", "/", event -> {

                    vertx.eventBus().consumer(Configuration.BUS_REGISTRY, data -> {
                        sendBus(event.textHandlerID(), data.body().toString());
                    });
                });
    }

    private void sendBus(String address, String data) {
        vertx.eventBus().send(address, data);
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        vertx.close();
    }
}
