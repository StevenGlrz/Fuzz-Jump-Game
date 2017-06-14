package com.fuzzjump.game.net.requests;

import com.google.gson.JsonObject;

/**
 * Created by Steven on 12/10/2014.
 */
public interface WebRequestCallback {

    void onResponse(JsonObject response);
}
