package com.fuzzjump.game.model.profile;

/**
 * Created by Steveadoo on 10/21/2015.
 */
public class FriendProfile extends Profile {

    public static final int STATUS_NONE = -1;
    public static final int STATUS_SENT = 0;
    public static final int STATUS_INCOMING = 1;
    public static final int STATUS_ACCEPTED = 2;
    private int status;

    public FriendProfile() {

    }

    public FriendProfile(String displayName, long userId) {
        this.setName(displayName);
        this.setUserId(userId);
    }

    public void setStatus(int status) {
        this.status = status;
        raiseEvent();
    }

    public int getStatus() {
        return status;
    }
}
