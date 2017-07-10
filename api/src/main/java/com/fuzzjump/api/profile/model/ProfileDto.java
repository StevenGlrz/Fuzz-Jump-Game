package com.fuzzjump.api.profile.model;

import com.fuzzjump.api.model.user.ApiProfile;

public class ProfileDto {

    private String displayName;
    private String userId;
    private short displayNameId;
    private ApiProfile profile;

    public String getDisplayName() {
        return displayName;
    }

    public String getUserId() {
        return userId;
    }

    public short getDisplayNameId() {
        return displayNameId;
    }

    public ApiProfile getProfile() {
        return profile;
    }
}
