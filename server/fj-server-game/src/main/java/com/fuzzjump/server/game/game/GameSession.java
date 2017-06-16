package com.fuzzjump.server.game.game;

import com.steveadoo.server.base.Session;

import java.util.UUID;

public class GameSession extends Session<GamePlayer> {

    public final int mapId;
    public final String seed;

    public GameSession(int mapId, String gameId, int max) {
        super(gameId, max);
        this.mapId = mapId;
        this.seed = UUID.randomUUID().toString();
    }

    @Override
    public boolean update() {
        return players.size() == 0;
    }

    @Override
    public void destroy() {
    }
}