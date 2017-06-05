package com.fuzzjump.game.net.requests;

import com.badlogic.gdx.Net;
import com.fuzzjump.game.model.profile.LocalProfile;

/**
 * Created by stephen on 8/22/2015.
 */
public abstract class AuthenticatedRequest extends WebRequest {

    private final LocalProfile profile;

    public AuthenticatedRequest(LocalProfile profile) {
        this.profile = profile;
    }

    public void fillHeader(Net.HttpRequest request) {
        request.setHeader("ProfileId", Long.toString(profile.getProfileId()));
        request.setHeader("Token", profile.getSessionKey());
        request.setTimeOut(5000);
    }

}
