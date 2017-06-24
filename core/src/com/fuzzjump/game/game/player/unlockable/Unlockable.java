package com.fuzzjump.game.game.player.unlockable;

public class Unlockable {

    private final UnlockableDefinition definition;
    private final int id;
    private int colorIndex;

    public Unlockable(UnlockableDefinition definition, int id, int colorIndex) {
        if (id == -1) {
            throw new IllegalArgumentException("Unlockable ID not allowed to be less than zero!");
        }
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
