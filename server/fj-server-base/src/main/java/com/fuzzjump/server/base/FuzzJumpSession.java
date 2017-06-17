package com.fuzzjump.server.base;

import java.util.ArrayList;
import java.util.List;

public abstract class FuzzJumpSession<T extends FuzzJumpPlayer> {

    public final int max;
    public final String id;

    protected boolean update;

    private int playerIndex = 0;
    protected final List<T> players = new ArrayList<T>();

    public FuzzJumpSession(String id, int max) {
        this.id = id;
        this.max = max;
    }

    /**
     * Updates the session
     * @return true if the session is over
     */
    public abstract boolean update();

    public abstract void destroy();

    public void addPlayer(T player) {
        player.setIndex(playerIndex++);
        player.setSession(this);
        players.add(player);
        update = true;
    }

    public void removePlayer(T player) {
        players.remove(player);
        update = true;
    }

    public boolean isEmpty() {
        return players.isEmpty();
    }

    public boolean filled() {
        return players.size() == max;
    }

    public List<T> getPlayers() {
        return players;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }
}