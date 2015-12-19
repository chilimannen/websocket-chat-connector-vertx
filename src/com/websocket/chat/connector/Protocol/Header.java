package com.websocket.chat.connector.Protocol;

/**
 * Created by Robin on 2015-12-18.
 * <p>
 * Header indicating the type of message.
 */
public class Header {
    private String action;
    private String actor;
    private String tag;

    public Header() {
    }

    public Header(String action) {
        this.action = action;
    }

    public Header(String action, String actor) {
        this.action = action;
        this.actor = actor;
    }

    public String consumeTag() {
        String data = tag;
        tag = null;
        return data;
    }

    public String getTag() {
        return tag;
    }

    public Header setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
