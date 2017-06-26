package com.fuzzjump.api.friends.model;

/**
 * Created by Steven Galarza on 6/24/2017.
 */
public class GenericFriendRequest {

    private final String userId;

    public GenericFriendRequest(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
