package com.fuzzjump.game.game.player;

import com.google.gson.JsonObject;

/**
 * Created by Steven on 6/8/2015.
 */
public interface Storable {
    public void load(JsonObject data);
    public void save(JsonObject data);
}
