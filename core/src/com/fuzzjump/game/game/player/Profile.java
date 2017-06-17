package com.fuzzjump.game.game.player;

/**
 * Created by Steven Galarza on 6/16/2017.
 */
public class Profile {

    private int playerIndex;
    private int profileId = -1;
    private int coins;
    private int ranking = 1;
    protected String name;
    private boolean ready;

    private long currentSeed;

    private Appearance appearance;

    public Profile() {
        this.appearance = new Appearance();
    }

    public long getProfileId() {
        return profileId;
    }

    public void setProfileId(int id) {
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
        return profileId != -1;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public boolean isReady() {
        return ready;
    }
}
