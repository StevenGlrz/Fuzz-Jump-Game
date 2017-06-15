package com.fuzzjump.game.service.model;

public abstract class Response {

    private boolean success;
    private String errorMessage;

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }


}
