package com.steveadoo.server.base;

import io.netty.channel.Channel;

public class Player {

    public final Channel channel;
    public final long profileId;

    public Session session;

    public boolean synced;
    public int index;

    public Player(Channel channel, long profileId) {
        this.channel = channel;
        this.profileId = profileId;
    }

}
