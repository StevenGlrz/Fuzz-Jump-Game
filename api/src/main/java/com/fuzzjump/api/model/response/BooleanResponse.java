package com.fuzzjump.api.model.response;

public class BooleanResponse extends Response<Boolean> {

    private Boolean body;

    @Override
    public Boolean getBody() {
        return body;
    }

}
