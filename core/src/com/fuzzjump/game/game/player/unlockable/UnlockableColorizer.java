package com.fuzzjump.game.game.player.unlockable;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fuzzjump.game.game.Assets;
import com.fuzzjump.libgdxscreens.VectorGraphicsLoader;
import com.fuzzjump.libgdxscreens.graphics.CColorGroup;

import java.util.Map;

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
    public TextureRegion[] colorize(Map<Integer, UnlockableDefinition> definitions) {
        TextureRegion[] regions = new TextureRegion[definitions.size()];
        for(int i = 0; i < regions.length; i++) {
            regions[i] = colorize(definitions.get(i), 0);
        }
        return regions;
    }


    public TextureRegion colorize(UnlockableDefinition definition, int colorIndex) {
        VectorGraphicsLoader.VectorDetail svgInfo = new VectorGraphicsLoader.VectorDetail(Assets.UNLOCKABLES_DIR + definition.getCategory() + "/" + definition.getId() + ".svg", "unlockable-" + definition.getCategory() + "-" + definition.getId(), "screen_width:.75", "asp");
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
