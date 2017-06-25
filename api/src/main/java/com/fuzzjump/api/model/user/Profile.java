package com.fuzzjump.api.model.user;

import com.fuzzjump.api.model.unlockable.Unlockable;

public class Profile {

    private int coins;
    private int experience;

    private Equip[] equips;
    private Unlockable[] unlockables;
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

    public Equip[] getEquips() {
        return equips;
    }

    public void setEquips(Equip[] equips) {
        this.equips = equips;
    }

    public Unlockable[] getUnlockables() {
        return unlockables;
    }

    public void setUnlockables(Unlockable[] unlockables) {
        this.unlockables = unlockables;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
