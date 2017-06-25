package com.fuzzjump.api.model.response;

public abstract class Response<T> {

    private boolean success;

    public boolean isSuccess() {
        return success;
    }

    public abstract T getBody();


}
