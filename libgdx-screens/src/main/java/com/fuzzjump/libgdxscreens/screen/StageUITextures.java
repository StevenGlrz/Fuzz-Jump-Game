package com.fuzzjump.libgdxscreens.screen;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.fuzzjump.libgdxscreens.Textures;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Steveadoo on 2/7/2016.
 */
public class StageUITextures {

    private final Map<String, TextureReferenceCounter> textureMap = new HashMap<>();

    private final Textures textures;

    public StageUITextures(Textures textures) {
        this.textures = textures;
    }

    public void clearHardRefs() {
        for(TextureReferenceCounter refCounter : textureMap.values()) {
            refCounter.hardrefregion = null;
        }
    }

    public Map<String, TextureReferenceCounter> getTexturesMap() {
        return textureMap;
    }

    public TextureRegion getTexture(String key) {
        return getTextureRefCounter(key).region.get();
    }

    public TextureRegion getTextureAbsolutePath(String path) {
        return getTextureRefCounterAbsolutePath(path).region.get();
    }

    public TextureRegionDrawable getTextureRegionDrawable(String key) {
        TextureReferenceCounter refCounter = getTextureRefCounter(key);
        if (refCounter.drawable == null)
            refCounter.drawable = new TextureRegionDrawable(getTexture(key));
        return refCounter.drawable;
    }

    public TextureRegionDrawable getTextureRegionAbsolutePathDrawable(String path) {
        TextureReferenceCounter refCounter = getTextureRefCounterAbsolutePath(path);
        if (refCounter.drawable == null)
            refCounter.drawable = new TextureRegionDrawable(getTexture(path));
        return refCounter.drawable;
    }

    private TextureReferenceCounter getTextureRefCounter(String key) {
        TextureReferenceCounter counter = null;
        if (!textureMap.containsKey(key) || (counter = textureMap.get(key)).region.get() == null)
            textureMap.put(key, counter = new TextureReferenceCounter(textures.getTexture(key)));
        counter.references++;
        return counter;
    }

    private TextureReferenceCounter getTextureRefCounterAbsolutePath(String path) {
        TextureReferenceCounter counter = null;
        if (!textureMap.containsKey(path) || (counter = textureMap.get(path)).region.get() == null)
            textureMap.put(path, counter = new TextureReferenceCounter(textures.getTextureFromPath(path)));
        counter.references++;
        return counter;
    }

    public Textures getTextures() {
        return textures;
    }

    public static class TextureReferenceCounter {

        protected TextureRegion hardrefregion;
        protected WeakReference<TextureRegion> region;
        protected int references;

        protected TextureRegionDrawable drawable;

        public TextureReferenceCounter(TextureRegion region) {
            this(region, false);
        }

        public TextureReferenceCounter(TextureRegion region, boolean hardref) {
            this.region = new WeakReference<>(region);
            if (hardref)
                this.hardrefregion = region;
        }

        public void inc() {
            references++;
        }

        public WeakReference<TextureRegion> getRegion() {
            return region;
        }
    }

}
