package com.fuzzjump.game.net.requests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
 * Created by Steven on 12/12/2014.
 */
public abstract class WebRequest implements Net.HttpResponseListener {

    public static final int SUCCESS = 1;

    public static final String API_URL = "https://kerpowgamesapi.azurewebsites.net/api/";
    public static final String PAYLOAD_KEY = "Payload";
    public static final String RESPONSE_KEY = "Response";

    protected WebRequestCallback callback;
    protected JsonObject parameters = new JsonObject();


    public void cancel() {
        callback = null;
    }

    @Override
    public void handleHttpResponse(Net.HttpResponse httpResponse) {
        if (callback == null)
            return;
        String result = httpResponse.getResultAsString();

        JsonParser parser = new JsonParser();
        final JsonObject response = parser.parse(result).getAsJsonObject();
        Gdx.app.postRunnable(new Runnable() {
            public void run() {
                callback.onResponse(response);
            }
        });
    }

    @Override
    public void failed(Throwable t) {
        callback.onResponse(null);
    }

    public abstract void connect(WebRequestCallback callback);
}
