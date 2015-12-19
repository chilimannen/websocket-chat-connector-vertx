package com.websocket.chat.connector;

/**
 * Created by Robin on 2015-12-19.
 *
 * Configuration file.
 */
class Configuration {
    public static final Integer CONNECTOR_PORT = 5030;
    public static final Integer DATABASE_PORT = 6070;
    public static final Integer REGISTRY_PORT = 7040;
    public static final String BUS_DATABASE_REQUEST = "database.request";
    public static final String BUS_DATABASE_RESPONSE = "database.response";
    public static final String BUS_REGISTRY = "registry.emit";
}
