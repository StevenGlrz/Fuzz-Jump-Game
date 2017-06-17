package com.fuzzjump.server.base;

import com.steveadoo.server.base.Player;

import io.netty.channel.Channel;

/**
 * Created by Steveadoo on 6/16/2017.
 */

public class FuzzJumpPlayer extends Player {

    private int index;
    private FuzzJumpSession session;

    private int profileId;

    public FuzzJumpPlayer(Channel channel) {
        super(channel);
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public FuzzJumpSession getSession() {
        return session;
    }

    public void setSession(FuzzJumpSession session) {
        this.session = session;
    }
}
