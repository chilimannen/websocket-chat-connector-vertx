package com.websocket.chat.connector;

/**
 * Created by Robin on 2015-12-19.
 * <p>
 * Used to pass parameters to a Message Handler.
 */
class HandlerParams {
    public String data;
    public ChatConnector connector;
    public String address;
    public String server;

    public HandlerParams(String data, ChatConnector connector, String address, String server) {
        this.data = data;
        this.connector = connector;
        this.address = address;
        this.server = server;
    }
}
