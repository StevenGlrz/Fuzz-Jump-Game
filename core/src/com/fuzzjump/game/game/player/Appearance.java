package com.fuzzjump.game.game.player;

import com.badlogic.gdx.utils.IntMap;
import com.fuzzjump.api.model.unlockable.Unlockable;
import com.fuzzjump.api.model.user.Equip;

/**
 * Created by Steven Galarza on 6/16/2017.
 */
public class Appearance {

    public static final String[] TITLES = {"Fuzzle", "Frame", "Head", "Eyes", "Face"};

    public static final int FUZZLE = 0;
    public static final int FRAME = 1;
    public static final int HEAD = 2;
    public static final int EYES = 3;
    public static final int FACE = 4;

    public static final int COUNT = 5;

    private int[] equips = new int[5];

    private IntMap<Unlockable> unlockables = new IntMap<>();

    private int[] equipSnapshot = new int[Appearance.COUNT];

    private IntMap<Unlockable> unlockableChanges = new IntMap<>();

    private boolean trackingChanges;

    public Appearance() {
    }

    public boolean isTracking() {
        return trackingChanges;
    }

    public void startTracking() {
        trackingChanges = true;

        System.arraycopy(equips, 0, equipSnapshot, 0, Appearance.COUNT);
    }

    public void stopTracking() {
        trackingChanges = false;
        for (int i = 0; i < equipSnapshot.length; i++) {
            equipSnapshot[i] = -1;
        }
        unlockableChanges.clear();
    }

    public void createUnlockables(Unlockable[] data) {
        for (Unlockable unlockable : data) {
            createUnlockable(unlockable);
        }
    }

    public Unlockable createUnlockable(Unlockable unlockable) {
        unlockables.put(unlockable.getId(), unlockable);
        return unlockable;
    }

    public void setEquip(int index, int id) {
        this.equips[index] = id;
    }

    public void setColorIndex(int itemId, int entryIndex) {
        Unlockable unlockable = getItem(itemId);
        if (unlockable != null) {
            unlockable.setColor(entryIndex);
            if (trackingChanges) {
                unlockableChanges.put(unlockable.getId(), unlockable);
            }
        }
    }

    public int getEquipId(int id) {
        return equips[id];
    }

    public Unlockable getEquip(int equip) {
        return unlockables.get(equips[equip]);
    }

    public int getColorIndex(int itemId) {
        Unlockable unlockable = getItem(itemId);
        return unlockable == null ? -1 : getItem(itemId).getColor();
    }

    /**
     * Retrieves an unlockable through its definition id
     * @param definitionId The definition id.
     * @return The unlockable with the requested definition id.
     */
    public Unlockable getItem(int definitionId) {
        for (Unlockable unlockable : unlockables.values()) {
            if (unlockable != null && unlockable.getDefinitionId() == definitionId) {
                return unlockable;
            }
        }
        return null;
    }

    public Unlockable[] getUnlockableChanges() {
        return null;
    }

    public Equip[] getEquipChanges() {
        int equipChanges = 0;
        for (int slot = 0; slot < Appearance.COUNT; slot++) {
            if (equips[slot] != equipSnapshot[slot]) {
                equipChanges++;
            }
        }
        if (equipChanges == 0) {
            return null;
        }
        Equip[] changes = new Equip[equipChanges];
        int index = 0;
        for (int slot = 0; slot < Appearance.COUNT; slot++) {
            if (equips[slot] != equipSnapshot[slot]) {
                changes[index++] = new Equip(slot, equips[slot]);
            }
        }
        return changes;
    }


    public IntMap<Unlockable> getUnlockables() {
        return unlockables;
    }

}
