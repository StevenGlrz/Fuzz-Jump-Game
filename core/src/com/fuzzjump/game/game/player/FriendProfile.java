package com.fuzzjump.game.game.player;

public class FriendProfile extends Profile {

    public static final int STATUS_NONE = -1;
    public static final int STATUS_SENT = 0;
    public static final int STATUS_INCOMING = 1;
    public static final int STATUS_ACCEPTED = 2;
    private int status;

    public FriendProfile() {

    }

    public FriendProfile(String displayName, int userId) {
        this.setName(displayName);
        this.setProfileId(userId);
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
