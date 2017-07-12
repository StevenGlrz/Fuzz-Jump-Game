package com.fuzzjump.api.invite.model;

public class InviteRequest {

    private String userId;
    private String gameId;
    private String ip;
    private int port;

    public InviteRequest(String userId, String gameId, String ip, int port) {
        this.userId = userId;
        this.gameId = gameId;
        this.ip = ip;
        this.port = port;
    }

    public String getUserId() {
        return userId;
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

}
