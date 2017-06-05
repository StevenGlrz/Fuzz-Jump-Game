package com.fuzzjump.game.model.profile;

import com.fuzzjump.game.game.player.Appearance;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class Profile {

    private LinkedList<ProfileChangeEventListener> changeListeners = new LinkedList<>();

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

    public void load(JSONObject data) throws JSONException {
        this.name = data.getString("Username");
        this.coins = data.getInt("Coins");
        this.userId = data.getLong("Id");

        if (data.has("GameProfile")) {
            JSONObject profileData = data.getJSONObject("GameProfile");
            this.ranking = profileData.getInt("Experience");
            this.profileId = profileData.getInt("ProfileId");
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
        raiseEvent();
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
        raiseEvent();
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
        raiseEvent();
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    public void setPlayerIndex(int playerIndex) {
        this.playerIndex = playerIndex;
        raiseEvent();
    }

    public void raiseEvent() {
        for (ProfileChangeEventListener listener : changeListeners)
            listener.profileChanged();
    }

    public void addChangeListener(ProfileChangeEventListener listener) {
        this.changeListeners.add(listener);
    }

    public void removeChangeListener(ProfileChangeEventListener listener) {
        this.changeListeners.remove(listener);
    }

    public boolean valid() {
        return userId != -1;
    }

    public interface ProfileChangeEventListener {

        void profileChanged();

    }
}