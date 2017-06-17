package com.fuzzjump.server.matchmaking.lobby;

import com.fuzzjump.server.base.FuzzJumpPlayer;
import com.steveadoo.server.base.Player;
import io.netty.channel.Channel;

public class LobbyPlayer extends FuzzJumpPlayer {

    private boolean ready;
    private int mapId = -1;
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

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }
}
