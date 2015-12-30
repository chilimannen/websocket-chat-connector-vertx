package com.websocket.chat.connector;

/**
 * Created by Robin on 2015-12-19.
 * <p>
 * Configuration file.
 */
public class Configuration {
    public static final Integer CONNECTOR_PORT = 5030;
    public static final Integer DATABASE_PORT = 6070;
    public static final Integer REGISTRY_PORT = 7040;
    public static final Integer LOGGER_PORT = 5454;
    public static final int PERSISTENCE_MESSAGE_BATCH = 500;
    public static final long BATCH_UPDATE = 500;
    public static Integer LOG_INTERVAL = 1000;
    public static final String BUS_LOGGER = "logging.upload";
    public static final String BUS_DATABASE_REQUEST = "database.request";
    public static final String BUS_DATABASE_RESPONSE = "database.response";
    public static final String BUS_REGISTRY = "registry.emit";
    public static final String REGISTER_NAME = "connector";
}
