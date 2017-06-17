package com.fuzzjump.game.persistence;

import com.google.gson.JsonObject;

/**
 * Created by Steven Galarza on 6/16/2017.
 */
public interface Storable {
    void load(JsonObject data);
    void save(JsonObject data);
}
