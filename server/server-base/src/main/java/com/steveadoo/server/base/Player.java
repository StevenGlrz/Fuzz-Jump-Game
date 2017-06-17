package com.steveadoo.server.base;

import io.netty.channel.Channel;

public abstract class Player {

    public final Channel channel;

    public Player(Channel channel) {
        this.channel = channel;
    }

}
