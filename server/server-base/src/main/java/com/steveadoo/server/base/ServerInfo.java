package com.steveadoo.server.base;

public class ServerInfo {

    private String authUri;

    //the port the load balancer does healthchecks on etc
    private int lbPort = -1;
    //the port that players use for direct connection
    private int privatePort;
    //the ip of this machine
    private String ip;

    /**
     * Constructs a ServerInfo that will listen on
     * the load balancer port and a private port.
     * The bootstrapper will find this machines ip with this
     * config
     * @param authUri The authuri to validate players with
     * @param loadBalancePort the load balancer port
     * @param privatePort the private port to listen on
     */
    public ServerInfo(String authUri,
                      int loadBalancePort,
                      int privatePort) {
        this.authUri = authUri;
        this.lbPort = loadBalancePort;
        this.privatePort = privatePort;
    }

    /**
     * Constructs a serverInfo that will
     * just do direct connections
     * @param authUri The authuri to validate the players with
     * @param privatePort The private port to listen on
     */
    public ServerInfo(String authUri,
                      int privatePort) {
        this.authUri = authUri;
        this.privatePort = privatePort;
    }

    public int getPrivatePort() {
        return privatePort;
    }

    public int getLbPort() {
        return lbPort;
    }

    public void setLbPort(int lbPort) {
        this.lbPort = lbPort;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAuthUri() {
        return authUri;
    }

}