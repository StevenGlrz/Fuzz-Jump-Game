package com.fuzzjump.game.service.model;

import com.google.gson.JsonObject;

public class Response {

    private boolean success;
    private JsonObject body;

    public JsonObject getBody() {
        return body;
    }

    public boolean isSuccess() {
        return success;
    }


}
