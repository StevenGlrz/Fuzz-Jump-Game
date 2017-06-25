package com.fuzzjump.api.model;

import com.google.gson.JsonElement;

public class Response {

    private boolean success;
    private JsonElement body;

    public JsonElement getBody() {
        return body;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isGood() {
        return success && body != null;
    }

}
