package com.websocket.chat.connector.Model;

import com.websocket.chat.connector.Protocol.Register;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Robin on 2015-12-18.
 * <p>
 * Server data stored in the registry.
 */
public class Server {
    private String address;
    private String name;
    private String ip;
    private Integer port;
    private Boolean full = false;
    private HashMap<String, Room> rooms = new HashMap<>();

    public Server(Register register, String ip, String address) {
        this.name = register.getName();
        this.port = register.getPort();
        this.ip = ip;
        this.address = address;
    }

    public String getAddress() {
        return this.address;
    }

    public String getName() {
        return name;
    }

    public String getIp() {
        return ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setFull(Boolean full) {
        this.full = full;
    }

    public HashMap<String, Room> getRooms() {
        return rooms;
    }

    public Boolean getFull() {
        return full;
    }
}
