package com.fuzzjump.game.net.requests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Steven on 12/11/2014.
 */
public class AuthenticationWebRequest extends WebRequest {

    private boolean hasKey;
    private long profileId;
    private String sessionKey;

    public AuthenticationWebRequest(String email, String password) {
        try {
            JSONObject kerpowMap = new JSONObject();
            kerpowMap.put("type", "Kerpow");
            kerpowMap.put("Password", password);

            parameters.put("Email", email);
            parameters.put("GameCode", "FuzzJump");
            parameters.put("LoginMethod", kerpowMap);
        } catch (JSONException e) {

        }
    }

    public AuthenticationWebRequest(long profileId, String sessionKey) {
        hasKey = true;
        this.profileId = profileId;
        this.sessionKey = sessionKey;
    }

    public AuthenticationWebRequest(FacebookProfile facebookProfile) {
        try {
            JSONObject facebookMap = new JSONObject();
            facebookMap.put("type", "Facebook");
            facebookMap.put("Token", facebookProfile.getAuthToken());
            facebookMap.put("Id", String.valueOf(facebookProfile.getId()));

            parameters.put("Email", facebookProfile.getEmail());
            parameters.put("GameCode", "FuzzJump");
            parameters.put("LoginMethod", facebookMap);
        } catch (JSONException e) {

        }
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
