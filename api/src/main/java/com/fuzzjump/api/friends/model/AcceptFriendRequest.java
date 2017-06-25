package com.fuzzjump.api.friends.model;

/**
 * Created by Steven Galarza on 6/24/2017.
 */
public class AcceptFriendRequest {

    private final String userId;

    public AcceptFriendRequest(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
