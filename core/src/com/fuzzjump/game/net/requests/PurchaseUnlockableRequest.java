package com.fuzzjump.game.net.requests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.fuzzjump.game.model.profile.LocalProfile;
import com.fuzzjump.game.model.profile.Profile;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by stephen on 8/22/2015.
 */
public class PurchaseUnlockableRequest extends AuthenticatedRequest {

    public PurchaseUnlockableRequest(LocalProfile profile, int unlockableDefinitionId) {
        super(profile);
        try {
            parameters.put("UnlockableDefinitionId", unlockableDefinitionId);
        } catch (JSONException e) {
            //welp
            e.printStackTrace();
        }
    }

    @Override
    public void connect(WebRequestCallback callback) {
        this.callback = callback;
        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.POST);
        request.setUrl(API_URL + "fuzzjump/purchaseunlockable");
        fillHeader(request);
        request.setHeader("Content-Type", "application/json");
        request.setContent(parameters.toString());
        Gdx.net.sendHttpRequest(request, this);
    }

    @Override
    public void failed(Throwable throwable) {
        Gdx.app.postRunnable(new Runnable() {
            public void run() {
                callback.onResponse(null);
            }
        });
    }

    @Override
    public void cancelled() {
        Gdx.app.postRunnable(new Runnable() {
            public void run() {
                callback.onResponse(null);
            }
        });
    }

}
