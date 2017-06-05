package com.fuzzjump.game.net.requests;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.fuzzjump.game.model.profile.LocalProfile;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SearchUsersRequest extends AuthenticatedRequest {

    private final String username;

    public SearchUsersRequest(LocalProfile profile, String username) {
        super(profile);
        this.username = username;
    }

    @Override
    public void connect(WebRequestCallback callback) {
        try {
            this.callback = callback;
            Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.GET);
            request.setUrl(API_URL + "search/searchusers?displayname=" + URLEncoder.encode(username, "UTF-8"));
            fillHeader(request);
            request.setHeader("Content-Type", "application/json");
            Gdx.net.sendHttpRequest(request, this);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failed(Throwable throwable) {

    }

    @Override
    public void cancelled() {

    }
}
