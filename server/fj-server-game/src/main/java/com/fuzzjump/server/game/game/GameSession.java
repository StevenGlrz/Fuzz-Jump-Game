package com.fuzzjump.server.game.game;

import com.fuzzjump.server.base.FuzzJumpSession;

import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

public class GameSession extends FuzzJumpSession<GamePlayer> {

    public final int mapId;
    public final String seed;

    private ScheduledFuture<?> tickFuture;

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

    public ScheduledFuture<?> getTickFuture() {
        return tickFuture;
    }

    public void setTickFuture(ScheduledFuture<?> tickFuture) {
        this.tickFuture = tickFuture;
    }
}
