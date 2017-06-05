package com.fuzzjump.game.model.specials;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.fuzzjump.game.game.ingame.IngameStage;
import com.fuzzjump.game.game.ingame.actors.Player;

/**
 * Kerpow Games, LLC
 * Created by stephen on 2/21/2015.
 */
public class AntiGravSpecial extends Special {

    @Override
    public void init() {

    }

    @Override
    public void activate(Player player) {
        player.velocityModifier.y = .65f;
    }

    @Override
    public void deactivate(Player player) {
        player.velocityModifier.y = 1f;
    }

    @Override
    public void onPreDraw(Player player, Batch batch, float frameTime) {

    }

    @Override
    public void onPostDraw(Player player, Batch batch, float frameTime) {

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
