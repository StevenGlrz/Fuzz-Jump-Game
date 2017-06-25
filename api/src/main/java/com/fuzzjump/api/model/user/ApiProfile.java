package com.fuzzjump.api.model.user;

import com.fuzzjump.api.model.unlockable.ApiUnlockable;

public class ApiProfile {

    private int coins;
    private int experience;

    private ApiEquip[] equips;
    private ApiUnlockable[] unlockables;
    private int id;

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public ApiEquip[] getEquips() {
        return equips;
    }

    public void setEquips(ApiEquip[] equips) {
        this.equips = equips;
    }

    public ApiUnlockable[] getUnlockables() {
        return unlockables;
    }

    public void setUnlockables(ApiUnlockable[] unlockables) {
        this.unlockables = unlockables;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
