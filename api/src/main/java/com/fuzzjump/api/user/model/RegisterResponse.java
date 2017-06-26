package com.fuzzjump.api.user.model;

import com.fuzzjump.api.model.response.Response;
import com.fuzzjump.api.model.user.ApiProfile;

/**
 * Created by Steveadoo on 6/24/2017.
 */

public class RegisterResponse extends Response {

    private RegisterBody body;

    public RegisterBody getBody() {
        return body;
    }

    public static class RegisterBody {

        private String userId;
        private String displayName;
        private int nameId;
        private String username;
        private String password;
        private ApiProfile profile;

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
    }

}
