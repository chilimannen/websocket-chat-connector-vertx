package com.websocket.chat.connector.Protocol;

/**
 * Created by Robin on 2015-12-16.
 * <p>
 * Message to Register the chatserver with the backend.
 */
public class Register {
    private String name;
    private Header header;
    private Integer port;

    public Register() {
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }
}

