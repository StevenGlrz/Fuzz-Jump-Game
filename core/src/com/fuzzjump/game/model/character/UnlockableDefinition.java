package com.fuzzjump.game.model.character;

import com.steveadoo.customizetextures.CColorGroup;

/**
 * Kerpow Games, LLC
 * Created by stephen on 6/7/2015.
 */
public class UnlockableDefinition {

    private final String name;
    private final int cost;
    private final CColorGroup replaceGroup;
    private String[] allowedTags;
    private final int category;
    private final int id;
    private CColorGroup[] colorGroups;
    private UnlockableBound[] bounds;

    public UnlockableDefinition(int id, int category, String name, int cost, String[] allowedTags, CColorGroup replaceGroup) {
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

    public void setColorGroups(CColorGroup[] colorGroups) {
        this.colorGroups = colorGroups;
    }

    public CColorGroup[] getColorGroups() {
        return colorGroups;
    }

    public String[] getAllowedTags() {
        return allowedTags;
    }

    public CColorGroup getReplaceGroup() {
        return replaceGroup;
    }

    public void setBounds(UnlockableBound[] bounds) {
        this.bounds = bounds;
    }

    public UnlockableBound[] getBounds() {
        return bounds;
    }
}
