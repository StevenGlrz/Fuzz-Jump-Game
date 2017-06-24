package com.fuzzjump.game.game.player;

import com.google.gson.JsonObject;

public class FriendProfile extends Profile {

    public static final int STATUS_NONE = -1;
    public static final int STATUS_SENT = 0;
    public static final int STATUS_INCOMING = 1;
    public static final int STATUS_ACCEPTED = 2;
    private int status;

    public FriendProfile() {
    }

    @Override
    public void load(JsonObject data) {

    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
