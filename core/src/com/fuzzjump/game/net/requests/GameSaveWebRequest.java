package com.fuzzjump.game.net.requests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.fuzzjump.game.FuzzJump;
import com.fuzzjump.game.model.character.Unlockable;
import com.fuzzjump.game.model.profile.LocalProfile;
import com.fuzzjump.game.model.profile.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Steven on 4/11/2015.
 */
public class GameSaveWebRequest extends AuthenticatedRequest {

    public GameSaveWebRequest(LocalProfile profile, long[] equips, List<Unlockable> diffs) {
        super(profile);
        try {
            JSONArray equipsArray = new JSONArray();
            for (int i = 0; i < equips.length; i++) {
                JSONObject obj = new JSONObject();
                obj.put("Slot", i);
                obj.put("UnlockableId", equips[i]);
                equipsArray.put(obj);
            }
            parameters.put("ItemSlots", equipsArray);

            JSONArray unlockablesArray = new JSONArray();
            for (Unlockable unlockable : diffs) {
                JSONObject obj = new JSONObject();
                obj.put("ColorIndex", unlockable.getColorIndex());
                obj.put("UnlockableId", unlockable.getId());
            }
            parameters.put("Unlockables", unlockablesArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
