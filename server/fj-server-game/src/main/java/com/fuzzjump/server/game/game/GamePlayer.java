package com.fuzzjump.server.game.game;

import com.steveadoo.server.base.Player;

import io.netty.channel.Channel;

public class GamePlayer extends Player {

    public GamePlayer(Channel channel, long profileId) {
        super(channel, profileId);
    }
}
