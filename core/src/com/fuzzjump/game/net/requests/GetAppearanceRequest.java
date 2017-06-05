package com.fuzzjump.game.net.requests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;

public class GetAppearanceRequest extends WebRequest {

    public final long[] ids;
    public final boolean userIds;

    public GetAppearanceRequest(long[] ids, boolean userIds) {
        this.ids = ids;
        this.userIds = userIds;
    }

    @Override
    public void connect(WebRequestCallback callback) {
        this.callback = callback;
        Net.HttpRequest httpGet = new Net.HttpRequest(Net.HttpMethods.GET);
        httpGet.setUrl(API_URL + "fuzzjump/getappearances?userIds=" + userIds + buildIdString());
        System.out.println(httpGet.getUrl());
        httpGet.setHeader("Content-Type", "application/json");
        httpGet.setContent(parameters.toString());
        Gdx.net.sendHttpRequest(httpGet, this);
    }

    private String buildIdString() {
        StringBuilder bldr = new StringBuilder();
        for (int i = 0; i < ids.length; i++)
            bldr.append("&ids[" + i + "]=").append(ids[i]);
        bldr.append("&ids["+ids.length+"]=0");//terminator?
        return bldr.toString();
    }

    @Override
    public void failed(Throwable throwable) {

    }

    @Override
    public void cancelled() {

    }
}
