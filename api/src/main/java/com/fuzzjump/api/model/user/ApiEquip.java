package com.fuzzjump.api.model.user;

import com.fuzzjump.api.model.unlockable.ApiUnlockable;

public class ApiEquip {

    private int slot;
    private ApiUnlockable unlockable;

    public ApiEquip() {

    }

    public ApiEquip(int slot, ApiUnlockable unlockable) {
        this.slot = slot;
        this.unlockable = unlockable;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public ApiUnlockable getUnlockable() {
        return unlockable;
    }

    public void setUnlockable(ApiUnlockable unlockable) {
        this.unlockable = unlockable;
    }

}
