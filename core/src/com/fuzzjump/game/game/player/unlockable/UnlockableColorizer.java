package com.fuzzjump.game.game.player.unlockable;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fuzzjump.api.model.unlockable.Unlockable;
import com.fuzzjump.game.game.Assets;
import com.fuzzjump.libgdxscreens.VectorGraphicsLoader;
import com.fuzzjump.libgdxscreens.graphics.ColorGroup;
import com.fuzzjump.libgdxscreens.screen.StageUITextures;

import java.util.Map;

public class UnlockableColorizer {

    private final VectorGraphicsLoader vectorGraphicsLoader;
    private final UnlockableRepository unlockableDefinitions;

    public UnlockableColorizer(VectorGraphicsLoader vectorGraphicsLoader, UnlockableRepository unlockableDefinitions) {
        this.vectorGraphicsLoader = vectorGraphicsLoader;
        this.unlockableDefinitions = unlockableDefinitions;
    }

    public TextureRegion getColored(StageUITextures textures, UnlockableDefinition definition, int colorIndex, boolean hardref) {
        Map<String, StageUITextures.TextureReferenceCounter> textureMap = textures.getTexturesMap();

        String mapKey = "unlockable-" + definition.getCategory() + "-" + definition.getId() + "-" + colorIndex;
        if (textureMap.containsKey(mapKey) && textureMap.get(mapKey).getRegion().get() != null) {
            return textures.getTexture(mapKey);
        }
        StageUITextures.TextureReferenceCounter colorized = new StageUITextures.TextureReferenceCounter(getColorized(definition, colorIndex), hardref);
        textureMap.put(mapKey, colorized);
        colorized.inc();
        return colorized.getRegion().get();
    }

    public TextureRegion getColored(StageUITextures textures, Unlockable unlockable, boolean hardref) {
        if (unlockable == null) {
            return null;
        }
        return getColored(textures, definitionFor(unlockable), unlockable.getColor(), hardref);
    }

    public TextureRegion getColorized(UnlockableDefinition unlockableDefinition, int colorIndex) {
        return colorize(unlockableDefinition, colorIndex);
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
        ColorGroup group = null;
        if (definition.getColorGroups() != null && definition.getColorGroups().length > colorIndex)
            group = definition.getColorGroups()[colorIndex];
        //never cache unlockable (unless it is a fuzz)
        return vectorGraphicsLoader.load(svgInfo, group, definition.getReplaceGroup(), definition.getId() < Assets.FUZZLE_COUNT);
    }

    public TextureRegion colorize(Unlockable unlockable) {
        return colorize(definitionFor(unlockable), unlockable.getColor());
    }

    // This shouldn't be here. Need to come up with a better design later
    public UnlockableDefinition definitionFor(Unlockable unlockable) {
        return unlockableDefinitions.getDefinition(unlockable.getDefinitionId());
    }
}
