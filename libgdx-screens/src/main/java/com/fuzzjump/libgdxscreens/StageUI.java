package com.fuzzjump.libgdxscreens;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class StageUI extends Table {

    protected StageScreen stageScreen;
    protected StageUITextures textures;

    private Map<Integer, Actor> actors = new HashMap<>();

    public StageUI(Textures textures, Skin skin) {
        this(new StageUITextures(textures), skin);
    }

    public StageUI(StageUITextures textures, Skin skin) {
        super(skin);
        this.textures = textures;
    }

    public void register(int id, Actor actor) {
        register(id, actor, true);
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

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public <T extends Actor> T actor(Class<T> type, int id) {
        return type.cast(actors.get(id));
    }

    public <T extends Actor> T actor(int id) {
        return (T) actors.get(id);
    }

    public StageUITextures getTextures() {
        return textures;
    }

    public abstract void init();
    public abstract void backPressed();

    public Collection<Actor> getActors() {
        return actors.values();
    }

    public StageScreen getStageScreen() {
        return stageScreen;
    }

    /**
     * This method exist to avoid confusion with inner classes that have a parent class with getSkin()
     * but are inside a class that inherits StageUI
     * @return The game skin.
     */
    public Skin getGameSkin() {
        return super.getSkin();
    }

    public boolean remove() {
        boolean rem = super.remove();
        if (rem)
            textures.clearHardRefs();
        return rem;
    }
}