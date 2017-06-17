package com.steveadoo.server.base;

public class ServerInfo {

    public final int port;
    public final boolean validate;
    public final int validationTimeout;

    /**
     * Constructs a ServerInfo that will tell the server listen on the provided port
     * @param port The port to listen on
     */
    public ServerInfo(int port) {
        this.port = port;
        this.validate = false;
        this.validationTimeout = 0;
    }
    public ServerInfo(int port, int validationTimeout) {
        this.port = port;
        this.validate = true;
        this.validationTimeout = validationTimeout;
    }

}