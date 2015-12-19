package com.websocket.chat.connector;

import com.websocket.chat.connector.Protocol.Authenticate;
import com.websocket.chat.connector.Protocol.History;
import com.websocket.chat.connector.Protocol.RoomInfo;
import com.websocket.chat.connector.Protocol.Serializer;

/**
 * Created by Robin on 2015-12-19.
 * <p>
 * Handles callbacks from the database.
 */

enum DatabaseHandler {
    HISTORY {
        @Override
        void process(String data, ChatConnector connector) {
            History history = (History) Serializer.unpack(data, History.class);
            connector.sendBus(history.getHeader().consumeTag(), history);
        }
    },

    ROOM {
        @Override
        void process(String data, ChatConnector connector) {
            RoomInfo room = (RoomInfo) Serializer.unpack(data, RoomInfo.class);
            connector.sendBus(room.getHeader().consumeTag(), room);
        }
    },

    AUTHENTICATE {
        @Override
        void process(String data, ChatConnector connector) {
            Authenticate authenticate = (Authenticate) Serializer.unpack(data, Authenticate.class);
            connector.sendBus(authenticate.getHeader().consumeTag(), authenticate);
        }
    };

    abstract void process(String packet, ChatConnector connector);
}
