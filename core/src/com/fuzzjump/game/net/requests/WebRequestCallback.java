package com.fuzzjump.game.net.requests;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Steven on 12/10/2014.
 */
public interface WebRequestCallback {

    public void onResponse(JSONObject response);
}
