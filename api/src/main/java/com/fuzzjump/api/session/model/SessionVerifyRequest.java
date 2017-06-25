package com.fuzzjump.api.session.model;

public class SessionVerifyRequest {

    private String userId;
    private String feature;
    private String token;

    public SessionVerifyRequest(String userId, String feature, String token) {
        this.userId = userId;
        this.feature = feature;
        this.token = token;
    }

    public SessionVerifyRequest() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
