package com.fuzzjump.server.game.game;

import com.fuzzjump.server.base.FuzzJumpPlayer;
import com.steveadoo.server.base.Player;

import io.netty.channel.Channel;

public class GamePlayer extends FuzzJumpPlayer {

    private boolean loaded = false;

    public GamePlayer(Channel channel) {
        super(channel);
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }
}
