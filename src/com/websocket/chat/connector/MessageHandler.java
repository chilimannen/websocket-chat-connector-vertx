package com.websocket.chat.connector;

import com.websocket.chat.connector.Model.Server;
import com.websocket.chat.connector.Protocol.*;

/**
 * Created by Robin on 2015-12-19.
 * <p>
 * Maps messages to methods.
 */
enum MessageHandler {


    AUTHENTICATE {
        @Override
        void process(HandlerParams params) {
            Authenticate authenticate = (Authenticate) Serializer.unpack(params.data, Authenticate.class);
            authenticate.getHeader().setTag(params.address);
            params.connector.sendBus(Configuration.BUS_DATABASE_REQUEST, authenticate);
        }
    },

    ROOM_STATUS {
        @Override
        void process(HandlerParams params) {
            RoomEvent event = (RoomEvent) Serializer.unpack(params.data, RoomEvent.class);
            params.connector.setRoomStatus(event);
        }
    },

    SERVER_STATUS {
        @Override
        void process(HandlerParams params) {
            ServerEvent event = (ServerEvent) Serializer.unpack(params.data, ServerEvent.class);
            params.connector.setServerStatus(event);
        }
    },

    CHAT_MESSAGE {
        @Override
        void process(HandlerParams params) {
            Message message = (Message) Serializer.unpack(params.data, Message.class);
            params.connector.sendRoom(message, message.getRoom(), params.serverName);
        }
    },

    HISTORY {
        @Override
        void process(HandlerParams params) {
            History history = (History) Serializer.unpack(params.data, History.class);
            history.getHeader().setTag(params.address);
            params.connector.sendBus(Configuration.BUS_DATABASE_REQUEST, history);
        }
    },

    ROOM {
        @Override
        void process(HandlerParams params) {
            RoomInfo room = (RoomInfo) Serializer.unpack(params.data, RoomInfo.class);
            room.getHeader().setTag(params.address);
            params.connector.sendBus(Configuration.BUS_DATABASE_REQUEST, room);
        }
    },

    TOPIC {
        @Override
        void process(HandlerParams params) {
            Topic topic = (Topic) Serializer.unpack(params.data, Topic.class);
            params.connector.sendRoom(topic, topic.getRoom(), params.serverName);
            params.connector.sendBus(Configuration.BUS_DATABASE_REQUEST, topic);
        }
    },

    USER_EVENT {
        @Override
        void process(HandlerParams params) {
            UserEvent event = (UserEvent) Serializer.unpack(params.data, UserEvent.class);
            params.connector.sendRoom(event, event.getRoom(), params.serverName);
        }
    },

    SERVER_LIST {
        @Override
        void process(HandlerParams params) {
            ServerList serverlist = (ServerList) Serializer.unpack(params.data, ServerList.class);
            serverlist.setList(params.connector.getServerList().getList());
            params.connector.sendBus(params.address, serverlist);
        }
    },

    SERVER_REGISTER {
        @Override
        void process(HandlerParams params) {
            Register register = (Register) Serializer.unpack(params.data, Register.class);
            params.connector.registerChatServer(new Server(register, params.ip, params.address));
            params.server.setString(register.getName());
        }
    };

    abstract void process(HandlerParams params);
}
