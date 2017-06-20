package com.fuzzjump.game.game.player.unlockable;

import com.badlogic.gdx.math.Rectangle;
import com.fuzzjump.libgdxscreens.graphics.ColorGroup;

public class UnlockableDefinition {

    private final String name;
    private final int cost;
    private final ColorGroup replaceGroup;
    private final String[] allowedTags;
    private final int category;
    private final int id;
    private ColorGroup[] colorGroups;
    private Rectangle[] bounds;

    public UnlockableDefinition(int id, int category, String name, int cost, String[] allowedTags, ColorGroup replaceGroup) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.cost = cost;
        this.allowedTags = allowedTags;
        this.replaceGroup = replaceGroup;
    }

    public boolean validFuzzle(String[] checkTags) {
        for (String check : checkTags) {
            for (String thisCheck : allowedTags) {
                if (check.equalsIgnoreCase(thisCheck)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public int getCategory() {
        return category;
    }

    public int getId() {
        return id;
    }

    public void setColorGroups(ColorGroup[] colorGroups) {
        this.colorGroups = colorGroups;
    }

    public ColorGroup[] getColorGroups() {
        return colorGroups;
    }

    public String[] getAllowedTags() {
        return allowedTags;
    }

    public ColorGroup getReplaceGroup() {
        return replaceGroup;
    }

    public void setBounds(Rectangle[] bounds) {
        this.bounds = bounds;
    }

    public Rectangle[] getBounds() {
        return bounds;
    }
}
