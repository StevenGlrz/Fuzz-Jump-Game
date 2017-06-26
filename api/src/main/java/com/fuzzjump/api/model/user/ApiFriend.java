package com.fuzzjump.api.model.user;

/**
 * Created by Steven Galarza on 6/24/2017.
 */
public class ApiFriend {

    private int status;
    private String userId;
    private String displayName;
    private int displayNameId;
    private ApiProfile profile;

    public int getStatus() {
        return status;
    }

    public String getUserId() {
        return userId;
    }

    public ApiProfile getProfile() {
        return profile;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDisplayNameId() {
        return displayNameId;
    }
}
