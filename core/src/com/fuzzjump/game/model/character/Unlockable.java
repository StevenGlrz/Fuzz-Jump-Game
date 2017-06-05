package com.fuzzjump.game.model.character;

/**
 * Kerpow Games, LLC
 * Created by stephen on 4/7/2015.
 */
public class Unlockable {

    private final UnlockableDefinition definition;
    private final int id;
    private int colorIndex;

    public Unlockable(UnlockableDefinition definition, int id, int colorIndex) {
        this.definition = definition;
        this.id = id;
        this.colorIndex = colorIndex;
    }

    public UnlockableDefinition getDefinition() {
        return definition;
    }

    public int getId() {
        return id;
    }

    public int getColorIndex() {
        return colorIndex;
    }

    public void setColorIndex(int colorIndex) {
        this.colorIndex = colorIndex;
    }

    public String toString() {
        return "[id="+id+", colorIndex="+colorIndex+"]";
    }
}
