package com.fuzzjump.game.game;

/**
 * This class contains the information about the current state
 * of our fuzz jump session.
 *
 * Created by Steven Galarza on 6/19/2017.
 */
public class FuzzContext {

    private String gameId;
    private String gameSeed;
    private int gameMap;
    private String ip;
    private int port;
    private String sessionKey;

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getGameSeed() {
        return gameSeed;
    }

    public void setGameSeed(String gameSeed) {
        this.gameSeed = gameSeed;
    }

    public int getGameMap() {
        return gameMap;
    }

    public void setGameMap(int gameMap) {
        this.gameMap = gameMap;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getSessionKey() {
        return sessionKey;
    }
}
