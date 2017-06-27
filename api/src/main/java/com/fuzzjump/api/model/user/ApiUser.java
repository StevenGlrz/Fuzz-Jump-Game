package com.fuzzjump.api.model.user;

/**
 * Created by Steven Galarza on 6/26/2017.
 */
public class ApiUser {

    private String userId;
    private String displayName;
    private int nameId;
    private String username;
    private String password;
    private ApiProfile profile;

    public ApiUser(String userId, String displayName, int nameId, ApiProfile profile) {
        this.userId = userId;
        this.displayName = displayName;
        this.nameId = nameId;
        this.profile = profile;
    }

    public String getPassword() {
        return password;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getNameId() {
        return nameId;
    }

    public String getUsername() {
        return username;
    }

    public ApiProfile getProfile() {
        return profile;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = null;
    }
}
