package com.steveadoo.server.base;

import io.netty.channel.Channel;

public abstract class Player {

    private final Channel channel;

    public Player(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

}
