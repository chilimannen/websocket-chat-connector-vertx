package com.websocket.chat.connector.Protocol;

import com.websocket.chat.connector.Configuration;

/**
 * Created by Robin on 2015-12-28.
 * <p>
 * Measures in/out messages.
 */
public class IOLogger {
    private Integer in = 0;
    private Integer out = 0;
    private String name = Configuration.REGISTER_NAME;
    private String type = "logging.io";

    public IOLogger() {
    }

    public void in() {
        in += 1;
    }

    public void out() {
        out += 1;
    }

    public Integer getIn() {
        return in;
    }

    public void setIn(Integer in) {
        this.in = in;
    }

    public Integer getOut() {
        return out;
    }

    public void setOut(Integer out) {
        this.out = out;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void reset() {
        this.in = 0;
        this.out = 0;
    }
}
