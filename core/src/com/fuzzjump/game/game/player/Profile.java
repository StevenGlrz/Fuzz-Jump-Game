package com.fuzzjump.game.game.player;

import com.fuzzjump.api.model.user.Equip;
import com.fuzzjump.api.user.model.RegisterResponse;
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

    public void load(RegisterResponse.RegisterBody body) {

        userName = body.getUsername();
        displayName = body.getDisplayName();
        nameId = body.getNameId();
        userId = body.getUserId();
        coins = body.getProfile().getCoins();
        experience = body.getProfile().getExperience();
        for (int i = 0, n = body.getProfile().getEquips().length; i < n; i++) {
            Equip equip = body.getProfile().getEquips()[i];

            int slot = equip.getSlot();

            if (equip.getUnlockable() != null) {
                int unlockableId = equip.getUnlockable().getDefinitionId();
                int unlockableColor = equip.getUnlockable().getColor();
                appearance.setEquip(slot, unlockableId);
                appearance.createUnlockable(unlockableId, unlockableColor);
            } else {
                appearance.setEquip(slot, -1);
            }

        }
    }

    public String getUserId() {
        return userId;
    }

    public void loadFriends(JsonArray data) {
        friends.clear();
        System.out.println("Loading " + data.size() + " friends");
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
        return experience / 100; // TODO Formula for levels
    }

    public String getApiName() {
        return userName;
    }
}
