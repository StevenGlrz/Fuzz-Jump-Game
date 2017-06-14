package com.fuzzjump.game.net.requests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.fuzzjump.game.model.character.Unlockable;
import com.fuzzjump.game.model.profile.LocalProfile;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * Created by Steven on 4/11/2015.
 */
public class GameSaveWebRequest extends AuthenticatedRequest {

    public GameSaveWebRequest(LocalProfile profile, long[] equips, List<Unlockable> diffs) {
        super(profile);
        JsonArray equipsArray = new JsonArray();
        for (int i = 0; i < equips.length; i++) {
            JsonObject obj = new JsonObject();
            obj.addProperty("Slot", i);
            obj.addProperty("UnlockableId", equips[i]);
            equipsArray.add(obj);
        }
        parameters.add("ItemSlots", equipsArray);

        JsonArray unlockablesArray = new JsonArray();
        for (Unlockable unlockable : diffs) {
            JsonObject obj = new JsonObject();
            obj.addProperty("ColorIndex", unlockable.getColorIndex());
            obj.addProperty("UnlockableId", unlockable.getId());
        }
        parameters.add("Unlockables", unlockablesArray);
    }

    @Override
    public void connect(WebRequestCallback callback) {
        this.callback = callback;
        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.POST);
        request.setUrl(API_URL + "fuzzjump/saveprofile");
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
