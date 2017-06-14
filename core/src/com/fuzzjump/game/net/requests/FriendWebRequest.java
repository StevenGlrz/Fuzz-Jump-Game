package com.fuzzjump.game.net.requests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.fuzzjump.game.model.profile.LocalProfile;

public class FriendWebRequest extends AuthenticatedRequest {

    public FriendWebRequest(LocalProfile profile, long friendUserId, int status) {
        super(profile);
        parameters.addProperty("UserId", friendUserId);
        parameters.addProperty("Status", status);
    }

    @Override
    public void connect(WebRequestCallback callback) {
        this.callback = callback;

        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.POST);
        request.setUrl(API_URL + "friends/changestatus");
        request.setHeader("Content-Type", "application/json");
        fillHeader(request);
        request.setContent(parameters.toString());
        Gdx.net.sendHttpRequest(request, this);
    }

    @Override
    public void cancelled() {
    }
}
