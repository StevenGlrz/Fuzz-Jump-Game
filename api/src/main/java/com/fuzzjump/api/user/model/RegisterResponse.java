package com.fuzzjump.api.user.model;

import com.fuzzjump.api.model.response.Response;
import com.fuzzjump.api.model.user.Profile;

/**
 * Created by Steveadoo on 6/24/2017.
 */

public class RegisterResponse extends Response<RegisterResponse.RegisterBody> {

    private RegisterBody body;

    @Override
    public RegisterBody getBody() {
        return body;
    }

    public static class RegisterBody {

        private String userId;
        private String displayName;
        private int nameId;
        private String username;
        private String password;
        private Profile profile;

        public String getPassword() {
            return password;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public int getNameId() {
            return nameId;
        }

        public void setNameId(int nameId) {
            this.nameId = nameId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Profile getProfile() {
            return profile;
        }

        public void setProfile(Profile profile) {
            this.profile = profile;
        }
    }

}
