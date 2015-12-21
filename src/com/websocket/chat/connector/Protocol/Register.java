package com.websocket.chat.connector.Protocol;

/**
 * Created by Robin on 2015-12-16.
 * <p>
 * Message to Register the chatserver with the backend.
 */
public class Register {
    public static final String ACTION = "register";
    private String name;
    private Header header;
    private Integer port;

    public Register() {
    }

    public Register(String name, Integer port) {
        this.name = name;
        this.port = port;
        this.header = new Header(ACTION);
    }

    public Integer getPort() {
        return port;
    }

    public Register setPort(Integer port) {
        this.port = port;
        return this;
    }

    public String getName() {
        return name;
    }

    public Register setName(String name) {
        this.name = name;
        return this;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }
}

