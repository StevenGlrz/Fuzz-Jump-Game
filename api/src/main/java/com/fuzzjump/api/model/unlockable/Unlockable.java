package com.fuzzjump.api.model.unlockable;

/**
 * Created by Steveadoo on 6/24/2017.
 */

public class Unlockable {

    private int color;
    private int definitionId;

    public Unlockable() {

    }

    public Unlockable(int color, int definitionId) {
        this.color = color;
        this.definitionId = definitionId;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(int definitionId) {
        this.definitionId = definitionId;
    }

}