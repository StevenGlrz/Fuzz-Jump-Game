package com.fuzzjump.game.game.player;

import com.fuzzjump.game.FuzzJump;
import com.fuzzjump.game.model.character.Unlockable;
import com.fuzzjump.game.model.character.UnlockableDefinition;
import com.fuzzjump.game.util.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Steven on 6/8/2015.
 */
public class Appearance implements Storable {

    public static final String[] TITLES = {"Fuzzle", "Frame", "Head", "Eyes", "Face"};

    private LinkedList<AppearanceChangeListener> changeListeners = new LinkedList<>();
    private long[] equips = new long[5];

    private Map<Integer, Unlockable> unlockables = new HashMap<>();
    private Map<Integer, Integer> colorIndexSnapshot = new HashMap<>();

    private long[] equipSnapshot;

    public Appearance() {

    }

    @Override
    public void load(JsonObject data) {
        JsonArray itemsArray = data.get("ItemSlots").getAsJsonArray();
        JsonArray unlocksArray = data.get("Unlockables").getAsJsonArray();
        equipSnapshot = null;
        colorIndexSnapshot = null;
        for (int i = 0; i < itemsArray.size(); i++) {
            JsonObject itemObj = itemsArray.get(i).getAsJsonObject();
            int slot = itemObj.get("Slot").getAsInt();
            if (!itemObj.has("UnlockableId") || itemObj.get("UnlockableId").getAsString().equalsIgnoreCase("null"))
                equips[slot] = -1;
            else
                equips[slot] = itemObj.get("UnlockableId").getAsLong();
        }
        unlockables.clear();
        for (int i = 0; i < unlocksArray.size(); i++) {
            createUnlockable(unlocksArray.get(i).getAsJsonObject());
        }
        raiseEvent();
    }

    public void snapshot() {
        equipSnapshot = equips.clone();
        colorIndexSnapshot.clear();
        for (int i = 0; i < unlockables.size(); i++) {
            Unlockable u = unlockables.get(i);
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
            for (int i = 0; i < unlockables.size(); i++) {
                Unlockable u = unlockables.get(i);
                if (Utils.fallback(colorIndexSnapshot.get(u.getId()), u.getColorIndex()) != u.getColorIndex()) {
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
                u.setColorIndex(Utils.fallback(colorIndexSnapshot.get(u.getId()), u.getColorIndex()));
            }
        }
        raiseEvent();
    }

    public List<Unlockable> getDiffs() {
        List<Unlockable> diffs = new LinkedList<>();
        if (colorIndexSnapshot.size() > 0) {
            for (int i = 0; i < unlockables.size(); i++) {
                Unlockable u = unlockables.get(i);
                if (Utils.fallback(colorIndexSnapshot.get(u.getId()), u.getColorIndex()) != u.getColorIndex()) {
                    diffs.add(u);
                }
            }
        }
        return diffs;
    }

    public Unlockable createUnlockable(JsonObject unlockObj) {
        int unlockableId = unlockObj.get("UnlockableId").getAsInt(); // was interpreted as long before, so need to test
        int colorIndex = unlockObj.get("ColorIndex").getAsInt();
        int definitionId = unlockObj.get("UnlockableDefinitionId").getAsInt();
        UnlockableDefinition definition = FuzzJump.Game.getUnlockableDefinitions().getDefinition(definitionId);
        Unlockable unlockable = new Unlockable(definition, unlockableId, colorIndex);
        unlockables.put(unlockable.getId(), unlockable);
        return unlockable;
    }

    @Override
    public void save(JsonObject data) {
        JsonArray itemsArray = new JsonArray();
        JsonArray unlocksArray = new JsonArray();
        for (int i = 0; i < equips.length; i++) {
            JsonObject equipObject = new JsonObject();
            equipObject.addProperty("Slot", i);

            Unlockable unlockable = getEquip(i);

            if (unlockable == null) {
                equipObject.addProperty("UnlockableId", "null");
            } else {
                equipObject.addProperty("UnlockableId", unlockable.getId());
            }

            itemsArray.add(equipObject);
        }
        for (int i = 0; i < unlockables.size(); i++) {
            Unlockable unlockable = unlockables.get(i);

            JsonObject unlocksObject = new JsonObject();
            unlocksObject.addProperty("ColorIndex", unlockable.getColorIndex());
            unlocksObject.addProperty("UnlockableId", unlockable.getId());
            unlocksObject.addProperty("UnlockableDefinitionId", unlockable.getDefinition().getId());
            unlocksArray.add(unlocksObject);
        }
        data.add("ItemSlots", itemsArray);
        data.add("Unlockables", unlocksArray);
    }

    public void setEquip(int index, long id) {
        this.equips[index] = id;
        raiseEvent();
    }

    public long getDefinitionId(int id) {
        return equips[id];
    }

    public Unlockable getEquip(int equip) {
        return getItem(equips[equip]);
    }

    public int getColorIndex(long itemId) {
        Unlockable unlockable = getItem(itemId);
        if (unlockable == null)
            return -1;
        return getItem(itemId).getColorIndex();
    }

    public long getItemId(UnlockableDefinition def) {
        for (int i = 0; i < unlockables.size(); i++) {
            Unlockable unlockable = unlockables.get(i);
            if (unlockable == null)
                continue;
            if (unlockable.getDefinition().getId() == def.getId()) {
                return unlockables.get(i).getId(); // TODO - Check if this is valid
            }

        }
        return -1;
    }

    public Unlockable getItem(UnlockableDefinition def) {
        return getItem(getItemId(def));
    }


    public void setColorIndex(long itemId, int entryIndex) {
        Unlockable unlockable = getItem(itemId);
        if (unlockable == null)
            return;
        unlockable.setColorIndex(entryIndex);
        raiseEvent();
    }

    public Unlockable getItem(long itemId) {
        return unlockables.get((int) itemId);
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

    public long[] getEquips() {
        return equips.clone();
    }

    public boolean loaded() {
        return unlockables.size() > 0;
    }

    public static class Equipment {
        public static final int FUZZLE = 0;
        public static final int FRAME = 1;
        public static final int HEAD = 2;
        public static final int EYES = 3;
        public static final int FACE = 4;

        public static final int COUNT = 5;
    }

    public interface AppearanceChangeListener {

        void appearanceChanged();

    }
}
