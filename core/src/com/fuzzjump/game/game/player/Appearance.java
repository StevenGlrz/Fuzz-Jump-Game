package com.fuzzjump.game.game.player;

import android.util.SparseArray;

import com.fuzzjump.game.FuzzJump;
import com.fuzzjump.game.model.character.Unlockable;
import com.fuzzjump.game.model.character.UnlockableDefinition;
import com.fuzzjump.game.model.character.UnlockableDefinitions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Steven on 6/8/2015.
 */
public class Appearance implements Storable {

    public static final String[] TITLES = {"Fuzzle", "Frame", "Head", "Eyes", "Face"};

    private LinkedList<AppearanceChangeListener> changeListeners = new LinkedList<>();
    private long[] equips = new long[5];
    private SparseArray<Unlockable> unlockables = new SparseArray<Unlockable>();

    private SparseArray<Integer> colorIndexSnapshot = new SparseArray<>();
    private long[] equipSnapshot;

    public Appearance() {

    }

    @Override
    public void load(JSONObject data) throws JSONException {
        JSONArray itemsArray = data.getJSONArray("ItemSlots");
        JSONArray unlocksArray = data.getJSONArray("Unlockables");
        equipSnapshot = null;
        colorIndexSnapshot = null;
        for (int i = 0; i < itemsArray.length(); i++) {
            JSONObject itemObj = itemsArray.getJSONObject(i);
            if (!itemObj.has("UnlockableId") || itemObj.getString("UnlockableId").equalsIgnoreCase("null"))
                equips[itemObj.getInt("Slot")] = -1;
            else
                equips[itemObj.getInt("Slot")] = itemObj.getLong("UnlockableId");
        }
        unlockables.clear();
        for (int i = 0; i < unlocksArray.length(); i++) {
            createUnlockable(unlocksArray.getJSONObject(i));
        }
        raiseEvent();
    }

    public void snapshot() {
        equipSnapshot = equips.clone();
        colorIndexSnapshot.clear();
        for (int i = 0; i < unlockables.size(); i++) {
            Unlockable u = unlockables.valueAt(i);
            colorIndexSnapshot.append(u.getId(), u.getColorIndex());
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
                Unlockable u = unlockables.valueAt(i);
                if (colorIndexSnapshot.get(u.getId(), u.getColorIndex()) != u.getColorIndex()) {
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
                Unlockable u = unlockables.valueAt(i);
                u.setColorIndex(colorIndexSnapshot.get(u.getId(), u.getColorIndex()));
            }
        }
        raiseEvent();
    }

    public List<Unlockable> getDiffs() {
        List<Unlockable> diffs = new LinkedList<>();
        if (colorIndexSnapshot.size() > 0) {
            for (int i = 0; i < unlockables.size(); i++) {
                Unlockable u = unlockables.valueAt(i);
                if (colorIndexSnapshot.get(u.getId(), u.getColorIndex()) != u.getColorIndex()) {
                    diffs.add(u);
                }
            }
        }
        return diffs;
    }

    public Unlockable createUnlockable(JSONObject unlockObj) {
        try {
            int unlockableId = (int) unlockObj.getLong("UnlockableId");
            int colorIndex = unlockObj.getInt("ColorIndex");
            int definitionId = unlockObj.getInt("UnlockableDefinitionId");
            UnlockableDefinition definition = FuzzJump.Game.getUnlockableDefinitions().getDefinition(definitionId);
            Unlockable unlockable = new Unlockable(definition, unlockableId, colorIndex);
            unlockables.append(unlockable.getId(), unlockable);
            return unlockable;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void save(JSONObject data) throws JSONException {
        JSONArray itemsArray = new JSONArray();
        JSONArray unlocksArray = new JSONArray();
        for(int i = 0; i < equips.length; i++) {
            JSONObject equipObject = new JSONObject();
            equipObject.put("Slot", i);
            Unlockable unlockable = getEquip(i);
            equipObject.put("UnlockableId", unlockable == null ? "null" : unlockable.getId());
            itemsArray.put(equipObject);
        }
        for (int i = 0; i < unlockables.size(); i++) {
            Unlockable unlockable = unlockables.valueAt(i);
            JSONObject unlocksObject = new JSONObject();
            unlocksObject.put("ColorIndex", unlockable.getColorIndex());
            unlocksObject.put("UnlockableId", unlockable.getId());
            unlocksObject.put("UnlockableDefinitionId", unlockable.getDefinition().getId());
            unlocksArray.put(unlocksObject);
        }
        data.put("ItemSlots", itemsArray);
        data.put("Unlockables", unlocksArray);

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
        for(int i = 0; i < unlockables.size(); i++) {
            Unlockable unlockable = unlockables.valueAt(i);
            if (unlockable == null)
                continue;
            if (unlockable.getDefinition().getId() == def.getId()) {
                return unlockables.keyAt(i);
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
