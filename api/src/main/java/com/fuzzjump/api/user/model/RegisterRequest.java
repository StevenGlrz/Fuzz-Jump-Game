package com.fuzzjump.api.user.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Steven Galarza on 6/21/2017.
 */
public class RegisterRequest {

    @SerializedName("Username")
    private final String username;

    public RegisterRequest(String username) {
        this.username = username;
    }
}
