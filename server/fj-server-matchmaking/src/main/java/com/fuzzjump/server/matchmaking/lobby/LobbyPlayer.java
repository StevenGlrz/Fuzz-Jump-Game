package com.fuzzjump.server.matchmaking.lobby;

import com.fuzzjump.server.base.FuzzJumpPlayer;

import io.netty.channel.Channel;

public class LobbyPlayer extends FuzzJumpPlayer {

    private boolean ready;
    private int selectedMap = -1;
    private boolean synced;

    public LobbyPlayer(Channel channel) {
        super(channel);
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public int getSelectedMap() {
        return selectedMap;
    }

    public void setSelectedMap(int selectedMap) {
        this.selectedMap = selectedMap;
    }

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }
}
