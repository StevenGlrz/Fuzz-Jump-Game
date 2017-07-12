package com.fuzzjump.api.invite.model;

import com.fuzzjump.api.model.response.Response;

public class GetInviteResponse extends Response {

    private Invite[] body;

    public Invite[] getBody() {
        return body;
    }

}
