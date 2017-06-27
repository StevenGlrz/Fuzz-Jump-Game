package com.fuzzjump.api.user.model;

import com.fuzzjump.api.model.response.Response;
import com.fuzzjump.api.model.user.ApiUser;

/**
 * Created by Steveadoo on 6/24/2017.
 */
public class RegisterResponse extends Response {

    private ApiUser body;

    public ApiUser getBody() {
        return body;
    }

}
