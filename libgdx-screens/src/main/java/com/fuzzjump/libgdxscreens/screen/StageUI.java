package com.fuzzjump.libgdxscreens.screen;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.IntMap;
import com.fuzzjump.libgdxscreens.Textures;

public abstract class StageUI extends Table {

    protected StageScreen stageScreen;
    protected StageUITextures textures;

    private final IntMap<Actor> actors = new IntMap<>();

    public StageUI(Textures textures, Skin skin) {
        this(new StageUITextures(textures), skin);
    }

    public StageUI(StageUITextures textures, Skin skin) {
        super(skin);
        this.textures = textures;
    }

    /**
     * Registers an actor for access between the screen, UI, and any other members with access to the UI.
     * NOTE: Actors can be replaced
     * @param id The id of the actor.
     * @param actor The actor.
     */
    public void register(int id, Actor actor) {
        register(id, actor, true);
    }

    public void register(final int id, final Actor actor, boolean registerListener) {
        register(id, actor, InputEvent.Type.touchUp, registerListener);
    }

    public void register(final int id, final Actor actor, final InputEvent.Type expectedEvent, boolean registerListener) {
        actors.put(id, actor);
        if (registerListener) {
            actor.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    if (event.getType() == expectedEvent) {
                        stageScreen.clicked(id, actor);
                    }
                }
            });
        }
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

    public void onShow() {

    }

    public abstract void init();
    public abstract void backPressed();

    public Iterable<Actor> getActors() {
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
        if (rem) {
            textures.clearHardRefs();
        }
        return rem;
    }
}