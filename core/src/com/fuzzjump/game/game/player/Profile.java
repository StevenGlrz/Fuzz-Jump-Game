package com.fuzzjump.game.game.player;

import com.fuzzjump.game.game.player.unlockable.UnlockableRepository;
import com.fuzzjump.game.util.Helper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Steven Galarza on 6/16/2017.
 */
public class Profile {

    private String userId;
    private String userName;
    private String displayName;
    private int nameId;
    private int playerIndex;
    private int profileId = -1;
    private int coins;
    private int experience;
    private Appearance appearance;

    private final List<FriendProfile> friends = new LinkedList<>();

    private boolean ready;

    public Profile() {
    }

    public Profile(UnlockableRepository definitions) {
        createAppearance(definitions);
    }

    public void createAppearance(UnlockableRepository definitions) {
        this.appearance = new Appearance(definitions);
    }

    public void load(JsonObject data) {
        JsonObject prof = data.getAsJsonObject("profile");
        JsonArray equips = prof.getAsJsonArray("equips");

        userName = data.get("username").getAsString();
        displayName = data.get("displayName").getAsString();
        nameId = data.get("nameId").getAsInt();
        userId = data.get("userId").getAsString();
        profileId = prof.get("id").getAsInt();
        coins = prof.get("coins").getAsInt();
        experience = prof.get("experience").getAsInt();
        for (int i = 0, n = equips.size(); i < n; i++) {
            JsonObject equip = equips.get(i).getAsJsonObject();
            JsonObject unlockable = Helper.getJsonObject(equip.get("unlockable"));

            int slot = equip.get("slot").getAsInt();

            if (unlockable != null) {
                int unlockableId = unlockable.get("definitionId").getAsInt();
                int unlockableColor = unlockable.get("color").getAsInt();
                appearance.setEquip(slot, unlockableId);
                appearance.createUnlockable(unlockableId, unlockableColor);
            } else {
                appearance.setEquip(slot, -1);
            }
        }
    }

    public void loadFriends(JsonArray data) {
        friends.clear();
        System.out.println("Loading " + data.size() + " friends");
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int id) {
        this.profileId = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public Appearance getAppearance() {
        return appearance;
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    public void setPlayerIndex(int playerIndex) {
        this.playerIndex = playerIndex;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public boolean isReady() {
        return ready;
    }

    public List<FriendProfile> getFriends() {
        return friends;
    }

    public int getLevel() {
        return experience / 100; // TODO Formula for levels
    }

    public String getApiName() {
        return userName;
    }
}
