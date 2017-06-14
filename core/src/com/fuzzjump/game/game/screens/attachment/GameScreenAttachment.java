package com.fuzzjump.game.game.screens.attachment;

public class GameScreenAttachment implements ScreenAttachment {

    private final int mapId;
    private final long seed;
    private final String ip;
    private final int port;
    private final String key;
    private final String gameId;
    private int rank;

    public GameScreenAttachment(int mapId, long seed, String ip, int port, String key, String gameId, int rank) {
        this.mapId = mapId;
        this.seed = seed;
        this.ip = ip;
        this.port = port;
        this.key = key;
        this.gameId = gameId;
        this.rank = rank;
    }

    public GameScreenAttachment(int mapId, long seed) {
        this.mapId = mapId;
        this.seed = seed;
        this.rank = 1;
        this.ip = null;
        this.port = -1;
        this.key = null;
        this.gameId = null;
    }


    public int getMapId() {
        return mapId;
    }

    public long getSeed() {
        return seed;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getGameId() {
        return gameId;
    }

    public String getKey() {
        return key;
    }

    public int getRank() {
        return rank;
    }

}
