package com.fuzzjump.game.model.specials;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Disposable;
import com.fuzzjump.game.game.ingame.IngameStage;
import com.fuzzjump.game.game.ingame.actors.Player;

/**
 * Specials are stateless
 * Created by Steven on 2/2/2015.
 */
public abstract class Special implements Disposable {

    protected TextureAtlas atlas;

    public void init() {
        try {
            atlas = new TextureAtlas("data/specials/" + getClass().getSimpleName().toLowerCase() + ".pack");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void activate(Player player);

    public abstract void deactivate(Player player);

    public abstract void onPreDraw(Player player, Batch batch, float time);

    public abstract void onPostDraw(Player player, Batch batch, float time);

    public abstract void act(IngameStage stage, Player player, float delta, boolean addParticle);

    public abstract void onInputEvent(Player player, InputEvent event);

    public abstract long getDuration();

    @Override
    public void dispose() {
        if (atlas != null)
            atlas.dispose();
        atlas = null;
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

}
