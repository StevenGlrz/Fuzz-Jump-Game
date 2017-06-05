package com.fuzzjump.game.net.requests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.fuzzjump.game.FuzzJump;
import com.fuzzjump.game.model.profile.LocalProfile;

import org.json.JSONException;
import org.json.JSONObject;

public class FriendWebRequest extends AuthenticatedRequest {

    public FriendWebRequest(LocalProfile profile, long friendUserId, int status) {
        super(profile);
        try {
            parameters.put("UserId", friendUserId);
            parameters.put("Status", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
