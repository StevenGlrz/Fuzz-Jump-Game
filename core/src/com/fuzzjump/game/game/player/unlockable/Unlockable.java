package com.fuzzjump.game.game.player.unlockable;

public class Unlockable {

    private final int id;
    private int colorIndex;

    public Unlockable(int id, int colorIndex) {
        if (id < 0) {
            throw new IllegalArgumentException("Unlockable ID not allowed to be less than zero!");
        }
        this.id = id;
        this.colorIndex = colorIndex;
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
