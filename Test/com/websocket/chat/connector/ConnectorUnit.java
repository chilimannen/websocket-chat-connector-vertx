package com.websocket.chat.connector;

import com.websocket.chat.connector.Model.Server;
import com.websocket.chat.connector.Protocol.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.SendContext;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;

/**
 * Created by Robin on 2015-12-19.
 * <p>
 * Tests for the connector.
 */

@RunWith(VertxUnitRunner.class)
public class ConnectorUnit extends AbstractVerticle {
    private Vertx vertx = Vertx.vertx();

    @Rule
    public Timeout timeout = new Timeout(500);

    @Test
    public void shouldRegisterServerToRegistry(TestContext context) {
        final Async async = context.async();
        Server server = new Server(new Register("test.server.1", 9099), "localhost", "null");

        ChatConnector connector = getConnector(interceptor -> {
            ServerEvent event = (ServerEvent) Serializer.unpack(interceptor.message().body().toString(), ServerEvent.class);

            context.assertEquals(event.getName(), "test.server.1");
            context.assertEquals(event.getPort(), 9099);
            context.assertEquals(event.getStatus(), ServerEvent.ServerStatus.UP);
            context.assertEquals(event.getIp(), "localhost");
            async.complete();
        });

        connector.registerChatServer(server);
        context.assertTrue(connector.getServerList().getList().size() == 1);
    }


    private ChatConnector getConnector(Handler<SendContext> interceptor) {
        EventBus bus = vertx.eventBus();
        bus.addInterceptor(interceptor);
        return new ChatConnector(bus);
    }

    @Test
    public void shouldDeregisterServerFromRegistry(TestContext context) {
        final Async async = context.async();

        Server server = new Server(new Register("test.server.1", 9099), "localhost", "null");

        ChatConnector connector = getConnector(interceptor -> {
            ServerEvent event = (ServerEvent) Serializer.unpack(interceptor.message().body().toString(), ServerEvent.class);

            if (event.getStatus().equals(ServerEvent.ServerStatus.DOWN)) {
                context.assertEquals(event.getName(), "test.server.1");
                context.assertEquals(event.getStatus(), ServerEvent.ServerStatus.DOWN);
                async.complete();
            }
        });

        connector.registerChatServer(server);
        connector.deregisterChatServer(server.getName());
        context.assertTrue(connector.getServerList().getList().size() == 0);
    }

    @Test
    public void shouldSetServerToFull(TestContext context) {
        final Async async = context.async();

        Server server = new Server(new Register("test.server.1", 9099), "localhost", "null");

        ChatConnector connector = getConnector(interceptor -> {
            ServerEvent event = (ServerEvent) Serializer.unpack(interceptor.message().body().toString(), ServerEvent.class);

            if (event.getStatus().equals(ServerEvent.ServerStatus.FULL)) {
                context.assertEquals(event.getName(), "test.server.1");
                context.assertEquals(event.getStatus(), ServerEvent.ServerStatus.FULL);
                async.complete();
            }
        });

        connector.registerChatServer(server);
        connector.setServerStatus(new ServerEvent(server, ServerEvent.ServerStatus.FULL));
    }

    @Test
    public void shouldSetServerToReady(TestContext context) {
        final Async async = context.async();
        Server server = new Server(new Register("test.server.1", 9099), "localhost", "null");

        ChatConnector connector = getConnector(interceptor -> {
            ServerEvent event = (ServerEvent) Serializer.unpack(interceptor.message().body().toString(), ServerEvent.class);

            if (event.getStatus().equals(ServerEvent.ServerStatus.READY)) {
                context.assertEquals(event.getName(), "test.server.1");
                context.assertEquals(event.getStatus(), ServerEvent.ServerStatus.READY);
                async.complete();
            }
        });

        connector.registerChatServer(server);
        connector.setServerStatus(new ServerEvent(server, ServerEvent.ServerStatus.READY));
    }

    @Test
    public void registrationNameShouldBeUnique(TestContext context) {
        Server server = new Server(new Register("test.server.1", 9099), "localhost", "null");

        ChatConnector connector = getConnector(interceptor -> {
        });

        connector.registerChatServer(server);
        connector.registerChatServer(server);

        context.assertEquals(connector.getServerList().getList().size(), 1);
    }

    @Test
    public void shouldGetRegisteredServers(TestContext context) {
        Server server1 = new Server(new Register("test.server.1", 9099), "localhost", "null");
        Server server2 = new Server(new Register("test.server.2", 9099), "localhost", "null");

        ChatConnector connector = getConnector(interceptor -> {
        });

        connector.registerChatServer(server1);
        connector.registerChatServer(server2);

        context.assertEquals(connector.getServerList().getList().size(), 2);

        for (ServerInfo info : connector.getServerList().getList()) {
            context.assertEquals(info.getPort(), 9099);
            context.assertEquals(info.getFull(), false);
            context.assertEquals(info.getIp(), "localhost");
        }
    }

    @Test
    public void shouldAddRoom(TestContext context) {
        final Async async = context.async();
        Server server = new Server(new Register("test.server.1", 9099), "localhost", "null");

        ChatConnector connector = getConnector(interceptor -> {
            Packet packet = (Packet) Serializer.unpack(interceptor.message().body().toString(), Packet.class);

            if (packet.getAction().equals(RoomEvent.ACTION)) {
                RoomEvent event = (RoomEvent) Serializer.unpack(interceptor.message().body().toString(), RoomEvent.class);
                if (event.getStatus().equals(RoomEvent.RoomStatus.POPULATED)) {
                    context.assertEquals(event.getServer(), "test.server.1");
                    context.assertEquals(event.getRoom(), "room");
                    async.complete();
                }
            }
        });

        connector.registerChatServer(server);
        connector.setRoomStatus(new RoomEvent("test.server.1", "room", RoomEvent.RoomStatus.POPULATED));
    }


    @Test
    public void shouldSetRoomToDepleted(TestContext context) {
        final Async async = context.async();
        Server server = new Server(new Register("test.server.1", 9099), "localhost", "null");

        ChatConnector connector = getConnector(interceptor -> {
            Packet packet = (Packet) Serializer.unpack(interceptor.message().body().toString(), Packet.class);

            if (packet.getAction().equals(RoomEvent.ACTION)) {
                RoomEvent event = (RoomEvent) Serializer.unpack(interceptor.message().body().toString(), RoomEvent.class);
                if (event.getStatus().equals(RoomEvent.RoomStatus.DEPLETED)) {
                    context.assertEquals(event.getServer(), "test.server.1");
                    context.assertEquals(event.getRoom(), "room");
                    async.complete();
                }
            }
        });

        connector.registerChatServer(server);
        connector.setRoomStatus(new RoomEvent("test.server.1", "room", RoomEvent.RoomStatus.DEPLETED));
    }

    @Test
    public void shouldSendRoom(TestContext context) {
        final Async async = context.async();
        Server server = new Server(new Register("test.server.1", 9099), "localhost", "null");

        ChatConnector connector = getConnector(interceptor -> {
            Packet packet = (Packet) Serializer.unpack(interceptor.message().body().toString(), Packet.class);

            if (!interceptor.message().address().equals(Configuration.BUS_DATABASE_REQUEST))
                if (packet.getAction().equals(Message.ACTION)) {
                    Message message = (Message) Serializer.unpack(interceptor.message().body().toString(), Message.class);
                    context.assertEquals(message.getRoom(), "room");
                    context.assertEquals(message.getContent(), "texty_text");
                    context.assertEquals(message.getSender(), "user");
                    async.complete();
                }
        });

        connector.registerChatServer(server);
        connector.setRoomStatus(new RoomEvent("test.server.1", "room", RoomEvent.RoomStatus.POPULATED));
        connector.sendRoom(new Message("texty_text", "room").setSender("user"), "room", "/dev/null");
    }

    @Test
    public void shouldSplitHorizon(TestContext context) {
        final Async async = context.async();
        Server server = new Server(new Register("test.server.1", 9099), "localhost", "null");

        ChatConnector connector = getConnector(interceptor -> {
            Packet packet = (Packet) Serializer.unpack(interceptor.message().body().toString(), Packet.class);

            if (!interceptor.message().address().equals(Configuration.BUS_DATABASE_REQUEST))
                if (packet.getAction().equals(Message.ACTION)) {
                    Message message = (Message) Serializer.unpack(interceptor.message().body().toString(), Message.class);
                    context.assertEquals(message.getRoom(), "room");
                    context.assertEquals(message.getContent(), "get_this");
                    context.assertEquals(message.getSender(), "user");
                    async.complete();
                }
        });

        connector.registerChatServer(server);
        connector.setRoomStatus(new RoomEvent("test.server.1", "room", RoomEvent.RoomStatus.POPULATED));
        connector.sendRoom(new Message("ignore_this", "room").setSender("user").setCommand(true), "room", "test.server.1");
        connector.sendRoom(new Message("get_this", "room").setSender("user").setCommand(true), "room", "/dev/null");
    }
}
