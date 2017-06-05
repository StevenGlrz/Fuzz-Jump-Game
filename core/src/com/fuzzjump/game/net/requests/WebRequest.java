package com.fuzzjump.game.net.requests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Steven on 12/12/2014.
 */
public abstract class WebRequest implements Net.HttpResponseListener {

    public static final int SUCCESS = 1;

    public static final String API_URL = "https://kerpowgamesapi.azurewebsites.net/api/";
    public static final String PAYLOAD_KEY = "Payload";
    public static final String RESPONSE_KEY = "Response";

    protected WebRequestCallback callback;
    protected JSONObject parameters = new JSONObject();


    public void cancel() {
        callback = null;
    }

    @Override
    public void handleHttpResponse(Net.HttpResponse httpResponse) {
        if (callback == null)
            return;
        try {
            String result = httpResponse.getResultAsString();
            final JSONObject response = new JSONObject(result);
            Gdx.app.postRunnable(new Runnable() {
                public void run() {
                    callback.onResponse(response);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onResponse(null);
        }
    }

    @Override
    public void failed(Throwable t) {
        callback.onResponse(null);
    }

    public abstract void connect(WebRequestCallback callback);
}
