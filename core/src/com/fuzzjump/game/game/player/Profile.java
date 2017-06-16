package com.fuzzjump.game.game.player;

import com.google.gson.JsonObject;

/**
 * Created by Steven Galarza on 6/16/2017.
 */
public class Profile {

    protected int playerIndex;
    protected long userId = -1;
    protected long profileId = -1;
    protected int coins;
    protected int ranking = 1;
    protected String name;

    private long currentSeed;

    protected Appearance appearance;

    public Profile() {
        this.appearance = new Appearance();
    }

    public void load(JsonObject data) {
        this.name = data.get("Username").getAsString();
        this.coins = data.get("Coins").getAsInt();
        this.userId = data.get("Id").getAsLong();

        if (data.has("GameProfile")) {
            JsonObject profileData = data.get("GameProfile").getAsJsonObject();
            this.ranking = profileData.get("Experience").getAsInt();
            this.profileId = profileData.get("ProfileId").getAsInt();
            this.appearance.load(profileData);
        }
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long id) {
        this.userId = id;
    }

    public long getProfileId() {
        return profileId;
    }

    public void setProfileId(long id) {
        this.profileId = id;
    }


    public String getName() {
        return name;
    }

    public long getCurrentSeed() {
        return currentSeed;
    }

    public void setName(String displayName) {
        this.name = displayName;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public void setCurrentSeed(long currentSeed) {
        this.currentSeed = currentSeed;
    }

    public Appearance getAppearance() {
        return appearance;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    public void setPlayerIndex(int playerIndex) {
        this.playerIndex = playerIndex;
    }

    public boolean valid() {
        return userId != -1;
    }
}
