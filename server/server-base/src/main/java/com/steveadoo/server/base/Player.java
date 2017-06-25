package com.steveadoo.server.base;

import io.netty.channel.Channel;

public abstract class Player {

    private Channel channel;

    public Player(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

}
