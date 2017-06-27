package com.fuzzjump.game.game.player;

import com.badlogic.gdx.utils.IntMap;
import com.fuzzjump.api.model.unlockable.Unlockable;

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

    public Appearance() {
    }

    public void setUnlockables(Unlockable[] data) {
        for (Unlockable unlockable : data) {
            unlockables.put(unlockable.getId(), unlockable);
        }
    }

    public Unlockable createUnlockable(Unlockable unlockable) {
        unlockables.put(unlockable.getId(), unlockable);
        return unlockable;
    }

    public void setEquip(int index, int id) {
        this.equips[index] = id;
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

    public void setColorIndex(int itemId, int entryIndex) {
        Unlockable unlockable = getItem(itemId);
        if (unlockable != null) {
            unlockable.setColor(entryIndex);
        }
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

    public IntMap<Unlockable> getUnlockables() {
        return unlockables;
    }

}
