package com.fuzzjump.api.model.unlockable;

/**
 * Created by Steveadoo on 6/24/2017.
 */

public class Unlockable {

    private int id;
    private int color;
    private int definitionId;

    public Unlockable(int id, int color, int definitionId) {
        this.id = id;
        this.color = color;
        this.definitionId = definitionId;
    }

    public int getColor() {
        return color;
    }

    public int getDefinitionId() {
        return definitionId;
    }

    public int getId() {
        return id;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
