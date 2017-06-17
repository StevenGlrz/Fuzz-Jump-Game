package com.fuzzjump.server.game.game;

import com.fuzzjump.server.base.FuzzJumpPlayer;
import com.steveadoo.server.base.Player;

import io.netty.channel.Channel;

public class GamePlayer extends FuzzJumpPlayer {

    public GamePlayer(Channel channel) {
        super(channel);
    }
}
