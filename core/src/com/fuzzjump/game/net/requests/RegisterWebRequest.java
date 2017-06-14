package com.fuzzjump.game.net.requests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.fuzzjump.game.FuzzJump;
import com.fuzzjump.game.model.profile.FacebookProfile;
import com.fuzzjump.game.model.profile.Profile;
import com.google.gson.JsonObject;

/**
 * Kerpow Games, LLC
 * Created by stephen on 2/2/2015.
 */
public class RegisterWebRequest extends WebRequest {

    public RegisterWebRequest(String email, String username, String password) {
        JsonObject kerpowMap = new JsonObject();
        kerpowMap.addProperty("type", "Kerpow");
        kerpowMap.addProperty("Password", password);

        parameters.addProperty("Email", email);
        parameters.addProperty("Username", username);
        parameters.addProperty("GameCode", "FuzzJump");
        parameters.add("RegistrationMethod", kerpowMap);
    }

    public RegisterWebRequest(FacebookProfile facebookProfile) {
        Profile profile = FuzzJump.Game.getProfile();

        JsonObject facebookMap = new JsonObject();
        facebookMap.addProperty("type", "Facebook");
        facebookMap.addProperty("Token", facebookProfile.getAuthToken());
        facebookMap.addProperty("Id", String.valueOf(facebookProfile.getId()));

        parameters.addProperty("Email", facebookProfile.getEmail());
        parameters.addProperty("Username", facebookProfile.getName());
        parameters.addProperty("GameCode", "FuzzJump");
        parameters.add("RegistrationMethod", facebookMap);
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
