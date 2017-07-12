package com.fuzzjump.api.profile.model;

import com.fuzzjump.api.model.response.Response;

public class GetProfileResponse extends Response {

    private ProfileDto[] body;

    public ProfileDto[] getBody() {
        return body;
    }

}
