package com.fuzzjump.api.invite.model;

public class InviteDeclineRequest extends InviteRequest {

    private String token;

    public InviteDeclineRequest(String userId, Invite invite) {
        super(userId, invite.getGameId(), invite.getIp(), invite.getPort());
        this.token = invite.getToken();
    }

    private String getToken() {
        return token;
    }

}
