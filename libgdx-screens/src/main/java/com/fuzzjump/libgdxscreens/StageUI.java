package com.fuzzjump.libgdxscreens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class StageUI extends Table {

    protected StageScreen stageScreen;
    protected StageUITextures textures;

    private ArrayList<AfterRenderRunnable> nextRenderList = new ArrayList<>();

    private Map<Integer, Actor> actors = new HashMap<Integer, Actor>();

    public StageUI(Textures textures) {
        this.textures = new StageUITextures(textures);
    }

    public void register(int id, Actor actor) {
        register(id, actor, true);
    }

    public void register(final int id, final Actor actor, final boolean waitForTouchUp) {
        actors.put(id, actor);
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

    public java.util.Collection<Actor> getActors() {
        return actors.values();
    }

    public boolean remove() {
        boolean rem = super.remove();
        if (rem)
            textures.clearHardRefs();
        return rem;
    }

    private static class AfterRenderRunnable {

        private final Runnable runnable;

        public int renderCount = 0;

        public AfterRenderRunnable(Runnable runnable) {
            this.runnable = runnable;
        }

    }
}