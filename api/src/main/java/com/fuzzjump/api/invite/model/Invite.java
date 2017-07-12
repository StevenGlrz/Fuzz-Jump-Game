package com.fuzzjump.api.invite.model;

public class Invite {

    private String fromUserId;
    private String gameId;
    private String ip;
    private int port;
    private String token;

    public String getFromUserId() {
        return fromUserId;
    }

    public String getGameId() {
        return gameId;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getToken() {
        return token;
    }

}
