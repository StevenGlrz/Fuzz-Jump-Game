package com.fuzzjump.api.model.user;

import com.fuzzjump.api.model.unlockable.Unlockable;

public class Equip {

    private int slot;
    private Unlockable unlockable;

    public Equip() {

    }

    public Equip(int slot, Unlockable unlockable) {
        this.slot = slot;
        this.unlockable = unlockable;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public Unlockable getUnlockable() {
        return unlockable;
    }

    public void setUnlockable(Unlockable unlockable) {
        this.unlockable = unlockable;
    }

}
