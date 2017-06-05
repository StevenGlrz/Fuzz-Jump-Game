package com.fuzzjump.game.net.requests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.fuzzjump.game.model.profile.LocalProfile;

import org.json.JSONException;

public class GetFriendsWebRequest extends AuthenticatedRequest {

    public GetFriendsWebRequest(LocalProfile profile) {
        super(profile);
    }

    @Override
    public void connect(WebRequestCallback callback) {
        this.callback = callback;

        Net.HttpRequest httpGet = new Net.HttpRequest(Net.HttpMethods.POST);
        httpGet.setUrl(API_URL + "friends/getfriends");
        httpGet.setContent(parameters.toString());
        Gdx.net.sendHttpRequest(httpGet, this);
    }

    @Override
    public void cancelled() {
    }
}
