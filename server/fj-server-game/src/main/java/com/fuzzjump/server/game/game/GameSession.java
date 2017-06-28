package com.fuzzjump.server.game.game;

import com.fuzzjump.server.base.FuzzJumpSession;
import com.fuzzjump.server.common.messages.game.Game;

import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

public class GameSession extends FuzzJumpSession<GamePlayer> {

    public final int mapId;
    public final String seed;

    private boolean started = false;
    private boolean loaded = false;
    private boolean destroyed;

    private long lastTime;
    private float remainingTime = 2;

    private ScheduledFuture<?> future;

    public GameSession(int mapId, String gameId, int max) {
        super(gameId, max);
        this.mapId = mapId;
        this.seed = UUID.randomUUID().toString();
    }

    public void sendPlayers() {
        Game.GameReady.Builder readyBuilder = Game.GameReady.newBuilder();
        readyBuilder.setSendLoaded(!isLoaded());
        for (GamePlayer player : players) {
            readyBuilder.addPlayers(Game.Player.newBuilder().setPlayerIndex(player.getIndex()).setUserId(player.getUserId()).buildPartial());
        }
        for (GamePlayer player : players) {
            player.getChannel().writeAndFlush(readyBuilder.buildPartial());
        }
    }

    @Override
    public boolean update() {
        if (remainingTime > 0) {
            if (lastTime == 0) {
                lastTime = System.currentTimeMillis();
            }
            long time = System.currentTimeMillis() - lastTime;
            lastTime = System.currentTimeMillis();
            remainingTime -= time / 1000f;
            sendCountdown(remainingTime);
        }
        //send positions.
        return players.size() == 0;
    }

    private void sendCountdown(float remainingTime) {
        Game.Countdown.Builder countdownBuilder = Game.Countdown.newBuilder();
        countdownBuilder.setTime((int) remainingTime);
        for (GamePlayer player : players) {
            player.getChannel().writeAndFlush(countdownBuilder.buildPartial());
        }
    }

    @Override
    public void destroy() {
        destroyed = true;
        for(GamePlayer player : getPlayers()) {
            player.getChannel().disconnect();
        }
    }

    public boolean isStarted() {
        return started;
    }

    public boolean checkStart(boolean forceStart) {
        if (!forceStart && players.size() != max) {
            return false;
        }
        return started = true;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public boolean checkLoaded(boolean forceLoad) {
        if (!forceLoad) {
            for (GamePlayer player : players) {
                if (!player.isLoaded()) {
                    return false;
                }
            }
        }
        return loaded = true;
    }

    public ScheduledFuture<?> getFuture() {
        return future;
    }

    public void setFuture(ScheduledFuture<?> tickFuture) {
        this.future = tickFuture;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

}
