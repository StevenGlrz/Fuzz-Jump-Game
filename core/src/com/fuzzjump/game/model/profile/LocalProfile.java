package com.fuzzjump.game.model.profile;

import com.fuzzjump.game.FuzzJump;
import com.fuzzjump.game.game.player.Friends;

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
    public void load(JSONObject data) throws JSONException {
        super.load(data);
        JSONObject profileData = data.getJSONObject("GameProfile");
        this.sessionKey = profileData.getString("SessionKey");
        friends.load(data);
    }

    public void save() {
        JSONObject save = new JSONObject();
        try {
            save.put("Username", name);
            save.put("Coins", coins);
            save.put("Id", getUserId());

            friends.save(save);

            JSONObject profileData = new JSONObject();
            appearance.save(profileData);
            profileData.put("Experience", ranking);
            profileData.put("SessionKey", sessionKey);
            profileData.put("ProfileId", userId);

            save.put("GameProfile", profileData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
