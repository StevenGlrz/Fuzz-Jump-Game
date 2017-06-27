package com.fuzzjump.api.model.user;

public class Equip {

    private int slot;
    private int unlockableId;

    public Equip(int slot, int unlockableId) {
        this.slot = slot;
        this.unlockableId = unlockableId;
    }

    public void setUnlockableId(int unlockableId) {
        this.unlockableId = unlockableId;
    }

    public int getSlot() {
        return slot;
    }

    public int getUnlockableId() {
        return unlockableId;
    }

    @Override
    public String toString() {
        return "Equip{" + "slot=" + slot + ", unlockableId=" + unlockableId + "}";
    }
}
