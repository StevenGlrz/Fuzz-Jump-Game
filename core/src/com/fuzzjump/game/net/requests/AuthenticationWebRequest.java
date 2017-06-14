package com.fuzzjump.game.net.requests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.fuzzjump.game.model.profile.FacebookProfile;
import com.google.gson.JsonObject;

/**
 * Created by Steven on 12/11/2014.
 */
public class AuthenticationWebRequest extends WebRequest {

    private boolean hasKey;
    private long profileId;
    private String sessionKey;

    public AuthenticationWebRequest(String email, String password) {
        JsonObject kerpowMap = new JsonObject();
        kerpowMap.addProperty("type", "Kerpow");
        kerpowMap.addProperty("Password", password);

        parameters.addProperty("Email", email);
        parameters.addProperty("GameCode", "FuzzJump");
        parameters.add("LoginMethod", kerpowMap);
    }

    public AuthenticationWebRequest(long profileId, String sessionKey) {
        hasKey = true;
        this.profileId = profileId;
        this.sessionKey = sessionKey;
    }

    public AuthenticationWebRequest(FacebookProfile facebookProfile) {
        JsonObject facebookMap = new JsonObject();
        facebookMap.addProperty("type", "Facebook");
        facebookMap.addProperty("Token", facebookProfile.getAuthToken());
        facebookMap.addProperty("Id", String.valueOf(facebookProfile.getId()));

        parameters.addProperty("Email", facebookProfile.getEmail());
        parameters.addProperty("GameCode", "FuzzJump");
        parameters.add("LoginMethod", facebookMap);
    }


    @Override
    public void connect(WebRequestCallback callback) {
        this.callback = callback;

        Net.HttpRequest request;
        if (hasKey) {
            request = new Net.HttpRequest(Net.HttpMethods.GET);
            request.setUrl(API_URL + "user/getprofile");
            request.setHeader("ProfileId", Long.toString(profileId));
            request.setHeader("Token", sessionKey == null ? "" : sessionKey); // TODO sessionKey is null. Need to fix
        } else {
            request = new Net.HttpRequest(Net.HttpMethods.POST);
            request.setUrl(API_URL + "user/login");
            request.setContent(parameters.toString());
        }
        request.setHeader("Content-Type", "application/json");
        Gdx.net.sendHttpRequest(request, this);
    }

    @Override
    public void failed(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void cancelled() {

    }
}
