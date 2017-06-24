package com.fuzzjump.game.game.player;

import com.fuzzjump.game.game.player.unlockable.Unlockable;
import com.fuzzjump.game.game.player.unlockable.UnlockableDefinition;
import com.fuzzjump.game.game.player.unlockable.UnlockableRepository;
import com.fuzzjump.game.util.Helper;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Steven Galarza on 6/16/2017.
 */
public class Appearance {
    // TODO Clean up house

    public static final String[] TITLES = {"Fuzzle", "Frame", "Head", "Eyes", "Face"};

    public static final int FUZZLE = 0;
    public static final int FRAME = 1;
    public static final int HEAD = 2;
    public static final int EYES = 3;
    public static final int FACE = 4;

    public static final int COUNT = 5;

    private LinkedList<AppearanceChangeListener> changeListeners = new LinkedList<>();
    private int[] equips = new int[5];

    private Map<Integer, Unlockable> unlockables = new HashMap<>();
    private Map<Integer, Integer> colorIndexSnapshot = new HashMap<>();
    private final UnlockableRepository unlockableDefinitions;

    private int[] equipSnapshot;

    public Appearance(UnlockableRepository unlockableDefinitions) {
        this.unlockableDefinitions = unlockableDefinitions;
    }

    public void snapshot() {
        equipSnapshot = equips.clone();
        colorIndexSnapshot.clear();

        for (Unlockable u : unlockables.values()) {
            colorIndexSnapshot.put(u.getId(), u.getColorIndex());
        }
    }

    public boolean changed() {
        if (equipSnapshot != null) {
            for (int i = 0; i < equips.length; i++) {
                if (equips[i] != equipSnapshot[i])
                    return true;
            }
        }
        if (colorIndexSnapshot.size() > 0) {
            for (Unlockable u : unlockables.values()) {
                if (Helper.fallback(colorIndexSnapshot.get(u.getId()), u.getColorIndex()) != u.getColorIndex()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void reset() {
        if (equipSnapshot != null) {
            for (int i = 0; i < equips.length; i++) {
                equips[i] = equipSnapshot[i];
            }
        }
        if (colorIndexSnapshot.size() > 0) {
            for (int i = 0; i < unlockables.size(); i++) {
                Unlockable u = unlockables.get(i);
                u.setColorIndex(Helper.fallback(colorIndexSnapshot.get(u.getId()), u.getColorIndex()));
            }
        }
        raiseEvent();
    }

    public List<Unlockable> getDiffs() {
        List<Unlockable> diffs = new LinkedList<>();
        if (colorIndexSnapshot.size() > 0) {
            for (int i = 0; i < unlockables.size(); i++) {
                Unlockable u = unlockables.get(i);
                if (Helper.fallback(colorIndexSnapshot.get(u.getId()), u.getColorIndex()) != u.getColorIndex()) {
                    diffs.add(u);
                }
            }
        }
        return diffs;
    }

    public Unlockable createUnlockable(int id, int color) {
        if (id == -1) {
            return null;
        }
        Unlockable unlockable = new Unlockable(unlockableDefinitions.getDefinition(id), id, color);
        unlockables.put(unlockable.getId(), unlockable);
        return unlockable;
    }

    public void setEquip(int index, int id) {
        this.equips[index] = id;
        raiseEvent();
    }

    public int getDefinitionId(int id) {
        return equips[id];
    }

    public Unlockable getEquip(int equip) {
        return getItem(equips[equip]);
    }

    public int getColorIndex(int itemId) {
        Unlockable unlockable = getItem(itemId);
        return unlockable == null ? -1 : getItem(itemId).getColorIndex();
    }

    public int getItemId(UnlockableDefinition def) {
        for (int i = 0; i < unlockables.size(); i++) {
            Unlockable unlockable = unlockables.get(i);
            if (unlockable != null && unlockable.getDefinition().getId() == def.getId()) {
                return unlockables.get(i).getId(); // TODO - Check if this is valid
            }
        }
        return -1;
    }

    public Unlockable getItem(UnlockableDefinition def) {
        return getItem(getItemId(def));
    }

    public void setColorIndex(int itemId, int entryIndex) {
        Unlockable unlockable = getItem(itemId);
        if (unlockable != null) {
            unlockable.setColorIndex(entryIndex);
            raiseEvent();
        }
    }

    public Unlockable getItem(int itemId) {
        return unlockables.get(itemId);
    }

    public void raiseEvent() {
        for (AppearanceChangeListener listener : changeListeners)
            listener.appearanceChanged();
    }

    public void addChangeListener(AppearanceChangeListener listener) {
        this.changeListeners.add(listener);
    }

    public void removeChangeListener(AppearanceChangeListener listener) {
        this.changeListeners.remove(listener);
    }

    public interface AppearanceChangeListener {

        void appearanceChanged();

    }
}
