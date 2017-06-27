package com.fuzzjump.api.model.user;

import com.fuzzjump.api.model.unlockable.Unlockable;

public class ApiProfile {

    private int coins;
    private int experience;
    private Equip[] equips;
    private Unlockable[] unlockables;

    public ApiProfile(int coins, int experience, Equip[] equips, Unlockable[] unlockables) {
        this.coins = coins;
        this.experience = experience;
        this.equips = equips;
        this.unlockables = unlockables;
    }

    public int getCoins() {
        return coins;
    }

    public int getExperience() {
        return experience;
    }

    public Equip[] getEquips() {
        return equips;
    }

    public Unlockable[] getUnlockables() {
        return unlockables;
    }
}
