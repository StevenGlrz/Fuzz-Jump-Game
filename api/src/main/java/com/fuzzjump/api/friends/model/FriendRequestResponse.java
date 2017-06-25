package com.fuzzjump.api.friends.model;

import com.fuzzjump.api.model.response.Response;
import com.fuzzjump.api.model.user.ApiFriend;

/**
 * Created by Steven Galarza on 6/24/2017.
 */
public class FriendRequestResponse extends Response {

    private ApiFriend body;

    public ApiFriend getBody() {
        return body;
    }
}
