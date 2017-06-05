package com.fuzzjump.game.net.requests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.fuzzjump.game.FuzzJump;
import com.fuzzjump.game.model.profile.Profile;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Kerpow Games, LLC
 * Created by stephen on 2/2/2015.
 */
public class RegisterWebRequest extends WebRequest {

    public RegisterWebRequest(String email, String username, String password) {
        try {
            JSONObject kerpowMap = new JSONObject();
            kerpowMap.put("type", "Kerpow");
            kerpowMap.put("Password", password);

            parameters.put("Email", email);
            parameters.put("Username", username);
            parameters.put("GameCode", "FuzzJump");
            parameters.put("RegistrationMethod", kerpowMap);
        } catch (JSONException e) {

        }
    }

    public RegisterWebRequest(FacebookProfile facebookProfile) {
        Profile profile = FuzzJump.Game.getProfile();

        try {
            JSONObject facebookMap = new JSONObject();
            facebookMap.put("type", "Facebook");
            facebookMap.put("Token", facebookProfile.getAuthToken());
            facebookMap.put("Id", String.valueOf(facebookProfile.getId()));

            parameters.put("Email", facebookProfile.getEmail());
            parameters.put("Username", facebookProfile.getName());
            parameters.put("GameCode", "FuzzJump");
            parameters.put("RegistrationMethod", facebookMap);
        } catch (JSONException e) {

        }
    }


    @Override
    public void connect(WebRequestCallback callback) {

        this.callback = callback;

        Net.HttpRequest httpGet = new Net.HttpRequest(Net.HttpMethods.POST);
        httpGet.setUrl(API_URL + "user/register");
        httpGet.setHeader("Content-Type", "application/json");
        httpGet.setContent(parameters.toString());

        Gdx.net.sendHttpRequest(httpGet, this);
    }

    @Override
    public void failed(Throwable throwable) {

    }

    @Override
    public void cancelled() {

    }

}
