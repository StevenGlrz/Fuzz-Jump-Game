package com.fuzzjump.api.profile.model;

import com.fuzzjump.api.model.response.Response;
import com.fuzzjump.api.model.user.ApiProfile;

/**
 * Created by Steven Galarza on 6/27/2017.
 */
public class SaveProfileResponse extends Response {

    private ApiProfile body;

    public ApiProfile getBody() {
        return body;
    }
}
