package com.fuzzjump.api.model.response;

public class StringResponse extends Response<String> {

    private String body;

    @Override
    public String getBody() {
        return body;
    }

}
