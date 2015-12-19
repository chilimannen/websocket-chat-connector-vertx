package com.websocket.chat.connector.Model;

import com.websocket.chat.connector.Protocol.RoomEvent;

/**
 * Created by Robin on 2015-12-18.
 * <p>
 * Room data stored in the Registry.
 */
public class Room {
    private String room;

    public Room(String room) {
        this.room = room;
    }

    public Room(RoomEvent room) {
        this.room = room.getRoom();
    }

    public String getName() {
        return room;
    }

    public void setName(String name) {
        this.room = name;
    }
}
