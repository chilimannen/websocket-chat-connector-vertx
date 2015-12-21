package com.websocket.chat.connector;

import io.vertx.core.http.ServerWebSocket;

/**
 * Created by Robin on 2015-12-19.
 * <p>
 * Used to pass parameters to a Message Handler.
 */
class HandlerParams {
    public String data;
    public ChatConnector connector;
    public String address;
    public MutableString server;
    public String ip;
    public String serverName;

    public HandlerParams(String data, ChatConnector connector, ServerWebSocket event, MutableString server) {
        this.data = data;
        this.connector = connector;
        this.address = event.textHandlerID();
        this.server = server;
        this.ip = event.remoteAddress().host();
        this.serverName = server.getString();
    }
}
