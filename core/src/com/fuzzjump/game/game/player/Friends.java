package com.fuzzjump.game.game.player;

import com.fuzzjump.game.model.profile.FriendProfile;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Steven on 12/10/2014.
 */
public class Friends implements Storable {

    private List<FriendProfile> friends = new ArrayList<>();

    public Friends() {
    }

    @Override
    public void load(JsonObject data) {
        friends.clear();
        if (!data.has("Friends"))
            return;
        JsonArray friendsData = data.getAsJsonArray("Friends");
        for (int i = 0, n = friendsData.size(); i < n; i++) {
            JsonObject friendObject = friendsData.get(i).getAsJsonObject();
            int status = friendObject.get("Status").getAsInt();
            FriendProfile friend = new FriendProfile();
            friend.setName(friendObject.get("Username").getAsString());
            friend.setUserId(friendObject.get("UserId").getAsLong());
            friends.add(friend);
            friend.setStatus(status);
        }
    }

    @Override
    public void save(JsonObject data) {
        JsonArray friendsData = new JsonArray();
        for (FriendProfile friend : friends) {
            JsonObject friendJson = new JsonObject();
            friendJson.addProperty("Status", friend.getStatus());
            friendJson.addProperty("Username", friend.getName());
            friendJson.addProperty("UserId", friend.getUserId());
            friendsData.add(friendJson);
        }
        data.add("Friends", friendsData);
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
