package com.fuzzjump.game.game.player;

import com.badlogic.gdx.utils.IntMap;
import com.fuzzjump.api.model.unlockable.Unlockable;
import com.fuzzjump.api.model.user.ApiFriend;
import com.fuzzjump.api.model.user.ApiProfile;
import com.fuzzjump.api.model.user.ApiUser;
import com.fuzzjump.api.model.user.Equip;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Steven Galarza on 6/16/2017.
 */
public class Profile {

    private String userId;
    private String displayName;
    private int displayNameId;
    private int playerIndex;
    private int coins;
    private int experience;

    private final Appearance appearance;
    private final List<FriendProfile> friends = new LinkedList<>();

    private boolean ready;

    public Profile() {
        this.appearance = new Appearance();
    }

    public void loadUser(ApiUser user) {
        displayName = user.getDisplayName();
        displayNameId = user.getNameId();
        userId = user.getUserId();
        loadProfile(user.getProfile());
    }

    public void loadProfile(ApiProfile profile) {
        coins = profile.getCoins();
        experience = profile.getExperience();
        for (Equip equip : profile.getEquips()) {
            int id = equip.getUnlockableId();
            int slot = equip.getSlot();

            // id = 0 means there is no unlockable set for this equip slot
            appearance.setEquip(slot, id == 0 ? -1 : id);
        }
        appearance.createUnlockables(profile.getUnlockables());
    }

    public void loadFriends(ApiFriend[] mFriends) {
        friends.clear();
        for (ApiFriend friend : mFriends) {
            friends.add(new FriendProfile(friend));
        }
    }

    // Not elegant, but sigh
    public ApiUser save() {
        Equip[] equips = new Equip[Appearance.COUNT];
        for (int slot = 0; slot < Appearance.COUNT; slot++) {
            equips[slot] = new Equip(slot, appearance.getEquipId(slot));
        }

        // Don't ask
        IntMap<Unlockable> appUnlockables = appearance.getUnlockables();
        Unlockable[] unlockables = new Unlockable[appUnlockables.size];
        int index = 0;
        for (Unlockable u : appUnlockables.values()) {
            unlockables[index++] = u;
        }
        ApiProfile profile = new ApiProfile(coins, experience, equips, unlockables);
        return new ApiUser(userId, displayName, displayNameId, profile);
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

    public void setDisplayNameId(int displayNameId) {
        this.displayNameId = displayNameId;
    }

    public int getDisplayNameId() {
        return displayNameId;
    }
}
