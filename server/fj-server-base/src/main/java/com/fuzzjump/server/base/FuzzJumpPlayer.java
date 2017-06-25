package com.fuzzjump.server.base;

import com.steveadoo.server.base.Player;

import io.netty.channel.Channel;

/**
 * Created by Steveadoo on 6/16/2017.
 */

public class FuzzJumpPlayer extends Player {

    private int index;
    private String serverSessionKey;
    private FuzzJumpSession session;

    private String userId;

    public FuzzJumpPlayer(Channel channel) {
        super(channel);
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

    public String getServerSessionKey() {
        return serverSessionKey;
    }

    public void setServerSessionKey(String serverSessionKey) {
        this.serverSessionKey = serverSessionKey;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
