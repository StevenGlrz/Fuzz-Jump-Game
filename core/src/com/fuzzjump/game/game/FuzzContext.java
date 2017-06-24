package com.fuzzjump.game.game;

/**
 * This class contains the information about the current state
 * of our fuzz jump session.
 *
 * Created by Steven Galarza on 6/19/2017.
 */
public class FuzzContext {


    private String gameId;
    private int gameSeed;
    private int gameMap;


    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public int getGameSeed() {
        return gameSeed;
    }

    public void setGameSeed(int gameSeed) {
        this.gameSeed = gameSeed;
    }

    public int getGameMap() {
        return gameMap;
    }

    public void setGameMap(int gameMap) {
        this.gameMap = gameMap;
    }
}
