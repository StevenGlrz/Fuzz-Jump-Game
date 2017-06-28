package com.steveadoo.server.base;

public class ServerConfig {

    public final int port;
    public final boolean validate;
    public final int validationTimeout;

    /**
     * Constructs a ServerInfo that will tell the server listen on the provided port
     * @param port The port to listen on
     */
    public ServerConfig(int port) {
        this.port = port;
        this.validate = false;
        this.validationTimeout = 0;
    }

    public ServerConfig(int port, int validationTimeout) {
        this.port = port;
        this.validate = true;
        this.validationTimeout = validationTimeout;
    }

}