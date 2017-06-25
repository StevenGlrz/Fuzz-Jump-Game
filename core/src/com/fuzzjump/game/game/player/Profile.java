package com.fuzzjump.game.game.player;

import com.fuzzjump.api.model.unlockable.ApiUnlockable;
import com.fuzzjump.api.model.user.ApiEquip;
import com.fuzzjump.api.model.user.ApiFriend;
import com.fuzzjump.api.model.user.ApiProfile;
import com.fuzzjump.api.user.model.RegisterResponse;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Steven Galarza on 6/16/2017.
 */
public class Profile {

    private String userId;
    private String userName;
    private String displayName;
    private int displayNameId;
    private int playerIndex;
    private int coins;
    private int experience;
    private Appearance appearance;

    private final List<FriendProfile> friends = new LinkedList<>();

    private boolean ready;

    public Profile() {
        this.appearance = new Appearance();
    }

    public void loadUser(RegisterResponse.RegisterBody body) {
        userName = body.getUsername();
        displayName = body.getDisplayName();
        displayNameId = body.getNameId();
        userId = body.getUserId();
        loadProfile(body.getProfile());
    }

    public void loadProfile(ApiProfile profile) {
        coins = profile.getCoins();
        experience = profile.getExperience();
        for (int i = 0, n = profile.getEquips().length; i < n; i++) {
            ApiEquip equip = profile.getEquips()[i];
            ApiUnlockable unlockable = equip.getUnlockable();

            int slot = equip.getSlot();

            if (unlockable != null) {
                int unlockableId = unlockable.getDefinitionId();
                int unlockableColor = unlockable.getColor();
                appearance.setEquip(slot, unlockableId);
                appearance.createUnlockable(unlockableId, unlockableColor);
            } else {
                appearance.setEquip(slot, -1);
            }

        }
    }

    public void loadFriends(ApiFriend[] mFriends) {
        friends.clear();

        for (int i = 0, n = mFriends.length; i < n; i++) {
            friends.add(new FriendProfile(mFriends[i]));
        }

        System.out.println("Loaded " + friends.size() + " friends");
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String id) {
        this.userId = id;
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
        return experience;
    }

    public String getApiName() {
        return userName;
    }

    public void setDisplayNameId(int displayNameId) {
        this.displayNameId = displayNameId;
    }

    public int getDisplayNameId() {
        return displayNameId;
    }
}
