package com.fuzzjump.game.service.user.model;

/**
 * Created by Steven Galarza on 6/24/2017.
 */
public class FriendRequest {

    private final String username;
    private final int usernameId;

    public FriendRequest(String username, int usernameId) {
        this.username = username;
        this.usernameId = usernameId;
    }

    public String getUsername() {
        return username;
    }

    public int getUsernameId() {
        return usernameId;
    }
}
