package com.fuzzjump.api.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Steven Galarza on 6/23/2017.
 */
public class TokenResponse {

    @SerializedName("access_token")
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }
}
