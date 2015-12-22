package com.websocket.chat.connector;

import com.websocket.chat.connector.Model.Room;
import com.websocket.chat.connector.Model.Server;
import com.websocket.chat.connector.Protocol.*;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Robin on 2015-12-19.
 * <p>
 * Manages all the connected chatrooms, forwards events and messages to the
 * database and other connected chatrooms.
 */
public class ChatConnector implements Verticle {
    private Vertx vertx;
    private EventBus bus;
    private Map<String, MessageHandler> messageHandler = new HashMap<>();
    private Map<String, DatabaseHandler> databaseHandler = new HashMap<>();
    private Map<String, Server> servers = new HashMap<>();
    private Map<String, Map<String, Server>> rooms = new HashMap<>();

    @Override
    public Vertx getVertx() {
        return vertx;
    }

    public ChatConnector() {
    }

    public ChatConnector(EventBus bus) {
        this.bus = bus;
    }

    @Override
    public void init(Vertx vertx, Context context) {
        this.vertx = vertx;
        this.bus = vertx.eventBus();

        databaseHandler.put(History.ACTION, DatabaseHandler.HISTORY);
        databaseHandler.put(RoomInfo.ACTION, DatabaseHandler.ROOM);
        databaseHandler.put(Authenticate.ACTION, DatabaseHandler.AUTHENTICATE);

        messageHandler.put(Register.ACTION, MessageHandler.SERVER_REGISTER);
        messageHandler.put(RoomEvent.ACTION, MessageHandler.ROOM_STATUS);
        messageHandler.put(ServerEvent.ACTION, MessageHandler.SERVER_STATUS);

        messageHandler.put(Authenticate.ACTION, MessageHandler.AUTHENTICATE);
        messageHandler.put(RoomInfo.ACTION, MessageHandler.ROOM);
        messageHandler.put(ServerList.ACTION, MessageHandler.SERVER_LIST);
        messageHandler.put(UserEvent.ACTION, MessageHandler.USER_EVENT);
        messageHandler.put(History.ACTION, MessageHandler.HISTORY);
        messageHandler.put(Message.ACTION, MessageHandler.CHAT_MESSAGE);
        messageHandler.put(Topic.ACTION, MessageHandler.TOPIC);
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        startConnector();
        startDBListener();
    }

    /**
     * Handle callbacks from the database.
     */
    private void startDBListener() {
        vertx.eventBus().consumer(Configuration.BUS_DATABASE_RESPONSE, event -> {
            Packet packet = (Packet) Serializer.unpack(event.body().toString(), Packet.class);
            DatabaseHandler handler = databaseHandler.get(packet.getAction());

            if (handler != null) {
                handler.process(event.body().toString(), this);
            }
        });
    }

    /**
     * Handle messages from chatservers.
     */
    private void startConnector() {
        vertx.createHttpServer().websocketHandler(event -> {
            final MutableString server = new MutableString();

            event.handler(data -> {
                Packet packet = (Packet) Serializer.unpack(data.toString(), Packet.class);
                MessageHandler handler = messageHandler.get(packet.getAction());

                if (handler != null)
                    handler.process(new HandlerParams(data.toString(), this, event, server));
            });

            event.closeHandler(close -> {
                if (server.getString() != null)
                    deregisterChatServer(server.getString());
            });

        }).listen(Configuration.CONNECTOR_PORT);

        System.out.println("Listening for rooms on port " + Configuration.CONNECTOR_PORT);
    }

    /**
     * Removes a chatserver from the list of available, also deregisters
     * it on any rooms.
     *
     * @param server name of the server to be deregistered.
     */

    protected void deregisterChatServer(String server) {
        HashMap<String, Room> subscribed = servers.get(server).getRooms();

        for (Room room : subscribed.values()) {
            rooms.get(room.getName()).remove(server);

            if (rooms.get(room.getName()).isEmpty()) {
                sendBus(Configuration.BUS_REGISTRY, new RoomEvent(server, room.getName(), RoomEvent.RoomStatus.DEPLETED));
                removeEmptyRoom(room.getName());
            }
        }
        servers.remove(server);
        sendBus(Configuration.BUS_REGISTRY, new ServerEvent(server, ServerEvent.ServerStatus.DOWN));
    }

    private void removeEmptyRoom(String room) {
        if (rooms.get(room).isEmpty()) {
            sendBus(Configuration.BUS_DATABASE_REQUEST, new RoomEvent(room, RoomEvent.RoomStatus.DEPLETED));
        }
    }

    /**
     * Registers a server to the list of available.
     *
     * @param server to be registered.
     */
    protected void registerChatServer(Server server) {
        servers.put(server.getName(), server);
        sendBus(Configuration.BUS_REGISTRY, new ServerEvent(server, ServerEvent.ServerStatus.UP));
        System.out.println("Registered server " + server.getName());
    }

    protected void sendBus(String address, Object message) {
        bus.send(address, Serializer.pack(message));
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        vertx.close();
    }

    /**
     * Set the availability of a room on a specific server.
     * The registry service is notified of the change.
     *
     * @param room the room to have it status changed.
     */
    protected void setRoomStatus(RoomEvent room) {
        Server server = servers.get(room.getServer());

        if (server != null) {
            if (rooms.get(room.getRoom()) == null) {
                rooms.put(room.getRoom(), new HashMap<>());
            }

            if (room.getStatus().equals(RoomEvent.RoomStatus.POPULATED)) {
                server.getRooms().put(room.getRoom(), new Room(room));
                rooms.get(room.getRoom()).put(room.getServer(), server);
            } else {
                rooms.get(room.getRoom()).remove(room.getServer());
                server.getRooms().remove(room.getRoom());
                removeEmptyRoom(room.getRoom());
            }
            sendBus(Configuration.BUS_REGISTRY, room);
        }
    }


    /**
     * Sets the status of a server, may either be full or ready.
     *
     * @param event contains the server and its new state.
     */
    protected void setServerStatus(ServerEvent event) {
        Server server = servers.get(event.getName());

        if (server != null) {
            server.setFull(event.getStatus().equals(ServerEvent.ServerStatus.FULL));
            sendBus(Configuration.BUS_REGISTRY, event);
        }
    }

    /**
     * Sends a message to all subscribed servers on a room using split horizon.
     *
     * @param message to be sent.
     * @param room    to send the message to.
     * @param origin  server name to ignore.
     */
    protected void sendRoom(Object message, String room, String origin) {

        if (rooms.get(room) != null) {
            for (Server server : rooms.get(room).values()) {

                if (!server.getName().equals(origin))
                    sendBus(server.getAddress(), message);
            }

            sendBus(Configuration.BUS_DATABASE_REQUEST, message);
        }
    }

    /**
     * @return a list of all the servers connected to the connector.
     */
    protected ServerList getServerList() {
        ArrayList<ServerInfo> list = new ArrayList<>();

        for (Server server : servers.values())
            list.add(new ServerInfo(server));

        return new ServerList(list);
    }
}
