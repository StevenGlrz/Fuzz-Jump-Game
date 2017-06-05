package com.fuzzjump.game.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.fuzzjump.game.FuzzJump;
import com.fuzzjump.game.model.character.Unlockable;
import com.fuzzjump.game.model.character.UnlockableDefinition;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public abstract class StageUI extends Table {

    protected static final String UI_BACKGROUND = "ui_background";
    protected static final String UI_GROUND = "ui_ground";

    protected FuzzJump game;
    protected StageScreen stageScreen;
    protected Textures textures;

    private final HashMap<String, TextureReferenceCounter> textureMap = new HashMap<>();

    private ArrayList<AfterRenderRunnable> nextRenderList = new ArrayList<>();

    private Map<Integer, Actor> actors = new HashMap<Integer, Actor>();
    private Map<Integer, Object> context = new HashMap<Integer, Object>();

    public StageUI() {
    }

    public void register(int id, Actor actor) {
        register(id, actor, true);
    }

    public void context(int id, Object ctx) {
        context.put(id, ctx);
    }

    public void register(final int id, final Actor actor, final boolean waitForTouchUp) {
        actors.put(id, actor);
        actor.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (waitForTouchUp && event.getType() == InputEvent.Type.touchUp) {
                    stageScreen.clicked(id, actor);
                } else if (!waitForTouchUp && event.getType() == InputEvent.Type.touchDown) {
                    stageScreen.clicked(id, actor);
                }
            }
        });
    }

    public TextureRegion getColored(UnlockableDefinition unlockableDefinition, int colorIndex, boolean hardref) {
        String mapKey = "unlockable-"+unlockableDefinition.getCategory()+"-"+unlockableDefinition.getId()+"-"+colorIndex;
        if (textureMap.containsKey(mapKey) && textureMap.get(mapKey).region.get() != null) {
            return getTexture(mapKey);
        }
        TextureReferenceCounter colorized = new TextureReferenceCounter(textures.getColorized(unlockableDefinition, colorIndex), hardref);
        textureMap.put(mapKey, colorized);
        colorized.references++;
        return colorized.region.get();
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

    public TextureRegion getColored(Unlockable unlockable, boolean hardref) {
        if (unlockable == null)
            return null;
        return getColored(unlockable.getDefinition(), unlockable.getColorIndex(), hardref);
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

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (nextRenderList.isEmpty())
            return;
        else {
            for (int i = 0; i < nextRenderList.size();) {
                AfterRenderRunnable afr = nextRenderList.get(i);
                if (afr.renderCount++ > 5) {
                    afr.runnable.run();
                    nextRenderList.remove(i);
                } else {
                    i++;
                }
            }
        }
    }

    public void submitForAfterDraw(Runnable runnable) {
        nextRenderList.add(new AfterRenderRunnable(runnable));
    }

    public HashMap<String, TextureReferenceCounter> getTextures() {
        return textureMap;
    }

    public <T extends Actor> T actor(Class<T> type, int id) {
        return type.cast(actors.get(id));
    }

    public <T extends Actor> T actor(int id) {
        return (T) actors.get(id);
    }

    public <T> T context(int id) {
        return (T) context.get(id);
    }

    public FuzzJump getGame() {
        return game;
    }

    public abstract void init();
    public abstract void backPressed();

    public java.util.Collection<Actor> actors() {
        return actors.values();
    }

    public Skin getSkin() {
        return game.getSkin();
    }

    public void clearHardRefs() {
        for(TextureReferenceCounter refCounter : textureMap.values()) {
            refCounter.hardrefregion = null;
        }
    }

    public boolean remove() {
        boolean rem = super.remove();
        if (rem)
            clearHardRefs();
        return rem;
    }

    /**
     * Wraps a texture region so we can count how many references we have to it.
     * We wrap the textureregion in a weakreference so we don't hold on to it
     * if its disposed in the same screen(like if i change the color of an item, we want the old one
     * to be available for collection)
     */
    public static class TextureReferenceCounter {

        protected TextureRegion hardrefregion;
        protected WeakReference<TextureRegion> region;
        protected int references;

        protected TextureRegionDrawable drawable;

        public TextureReferenceCounter(TextureRegion region) {
            this(region, false);
        }

        public TextureReferenceCounter(TextureRegion region, boolean hardref) {
            this.region = new WeakReference(region);
            if (hardref)
                this.hardrefregion = region;
        }
    }

    private static class AfterRenderRunnable {

        private final Runnable runnable;

        public int renderCount = 0;

        public AfterRenderRunnable(Runnable runnable) {
            this.runnable = runnable;
        }

    }

}