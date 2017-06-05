package com.fuzzjump.game.model.specials;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.fuzzjump.game.game.ingame.IngameStage;
import com.fuzzjump.game.game.ingame.actors.Player;

/**
 * Kerpow Games, LLC
 * Created by stephen on 2/21/2015.
 */
public class BalloonsSpecial extends Special {

    @Override
    public void activate(Player player) {

    }

    @Override
    public void deactivate(Player player) {

    }

    @Override
    public void onPreDraw(Player player, Batch batch, float time) {
        TextureRegion region = atlas.findRegion("item-balloons");
        batch.draw(region, (player.getX() + player.getWidth() / 2) - region.getRegionWidth() / 2, (player.getY() + player.getHeight() / 2));
    }

    @Override
    public void onPostDraw(Player player, Batch batch, float time) {

    }

    @Override
    public void act(IngameStage stage, Player player, float delta, boolean addParticle) {
    }

    @Override
    public void onInputEvent(Player player, InputEvent event) {

    }

    @Override
    public long getDuration() {
        return 1250;
    }

}
