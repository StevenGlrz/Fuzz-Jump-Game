package com.fuzzjump.game.game;

import android.util.SparseArray;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fuzzjump.game.VectorGraphicsLoader;
import com.fuzzjump.game.model.character.Unlockable;
import com.fuzzjump.game.model.character.UnlockableDefinition;
import com.steveadoo.customizetextures.CColorGroup;

/**
 * Created by stephen on 8/21/2015.
 */
public class UnlockableColorizer {

    private final VectorGraphicsLoader vectorGraphicsLoader;

    public UnlockableColorizer(VectorGraphicsLoader vectorGraphicsLoader) {
        this.vectorGraphicsLoader = vectorGraphicsLoader;
    }

    /**
     * colorize the definitions with the default colors
     */
    public TextureRegion[] colorize(SparseArray<UnlockableDefinition> definitions) {
        TextureRegion[] regions = new TextureRegion[definitions.size()];
        for(int i = 0; i < regions.length; i++) {
            regions[i] = colorize(definitions.valueAt(i), 0);
        }
        return regions;
    }


    public TextureRegion colorize(UnlockableDefinition definition, int colorIndex) {
        VectorGraphicsLoader.VectorDetails svgInfo = new VectorGraphicsLoader.VectorDetails("unlockables/" + definition.getCategory() + "/" + definition.getId() + ".svg", "unlockable-" + definition.getCategory() + "-" + definition.getId(), "screen_width:.75", "asp");
        CColorGroup group = null;
        if (definition.getColorGroups() != null && definition.getColorGroups().length > colorIndex)
            group = definition.getColorGroups()[colorIndex];
        //never cache unlockables
        return vectorGraphicsLoader.load(svgInfo, group, definition.getReplaceGroup(), false);
    }

    public TextureRegion colorize(Unlockable unlockable) {
        return colorize(unlockable.getDefinition(), unlockable.getColorIndex());
    }
}
