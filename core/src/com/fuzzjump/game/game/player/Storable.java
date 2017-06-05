package com.fuzzjump.game.game.player;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Steven on 6/8/2015.
 */
public interface Storable {
    public void load(JSONObject data) throws JSONException;
    public void save(JSONObject data) throws JSONException;
}
