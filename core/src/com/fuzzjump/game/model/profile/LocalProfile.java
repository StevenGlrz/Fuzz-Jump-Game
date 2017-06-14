package com.fuzzjump.game.model.profile;

import com.badlogic.gdx.utils.Json;
import com.fuzzjump.game.FuzzJump;
import com.fuzzjump.game.game.player.Friends;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Steveadoo on 9/30/2015.
 */
public class LocalProfile extends PlayerProfile {

    private Friends friends;
    private String sessionKey;
    private FacebookProfile facebookProfile = new FacebookProfile();

    public LocalProfile() {
        this.friends = new Friends();
    }

    @Override
    public void load(JsonObject data) {
        super.load(data);
        JsonObject profileData = data.get("GameProfile").getAsJsonObject();
        this.sessionKey = profileData.get("SessionKey").getAsString();
        friends.load(data);
    }

    public void save() {
        JsonObject save = new JsonObject();

        // general
        save.addProperty("Username", name);
        save.addProperty("Coins", coins);
        save.addProperty("Id", getUserId());

        friends.save(save);

        // profile
        JsonObject profileData = new JsonObject();
        appearance.save(profileData);
        profileData.addProperty("Experience", ranking);
        profileData.addProperty("SessionKey", sessionKey);
        profileData.addProperty("ProfileId", userId);

        save.add("GameProfile", profileData);
        raiseEvent();

        FuzzJump.Game.getPreferences().put("data", save.toString());
    }


    public void clear() {
        FuzzJump.Game.getPreferences().clear();
        raiseEvent();
    }

    public Friends getFriends() {
        return friends;
    }

    public FacebookProfile getFacebookProfile() {
        return facebookProfile;
    }

    public String getSessionKey() {
        return sessionKey;
    }
}
