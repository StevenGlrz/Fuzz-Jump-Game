package com.fuzzjump.game.game.player;

import com.fuzzjump.game.model.profile.FriendProfile;
import com.fuzzjump.game.model.profile.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Steven on 12/10/2014.
 */
public class Friends implements Storable {

    private List<FriendProfile> friends = new ArrayList<>();

    public Friends() {
    }

    @Override
    public void load(JSONObject data) throws JSONException {
        friends.clear();
        if (!data.has("Friends"))
            return;
        JSONArray friendsData = data.getJSONArray("Friends");
        for (int i = 0, n = friendsData.length(); i < n; i++) {
            JSONObject friendObject = friendsData.getJSONObject(i);
            int status = friendObject.getInt("Status");
            FriendProfile friend = new FriendProfile();
            friend.setName(friendObject.getString("Username"));
            friend.setUserId(friendObject.getLong("UserId"));
            friends.add(friend);
            friend.setStatus(status);
        }
    }

    @Override
    public void save(JSONObject data) throws JSONException {
        JSONArray friendsData = new JSONArray();
        for (FriendProfile friend : friends) {
            JSONObject friendJson = new JSONObject();
            friendJson.put("Status", friend.getStatus());
            friendJson.put("Username", friend.getName());
            friendJson.put("UserId", friend.getUserId());
            friendsData.put(friendJson);
        }
        data.put("Friends", friendsData);
    }

    public List<FriendProfile> getFriends() {
        return friends;
    }

    public boolean contains(long userId) {
        for (FriendProfile friend :
                friends) {
            if (friend.getUserId() == userId)
                return true;
        }
        return false;
    }

}
