package com.fuzzjump.game.model.specials;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.fuzzjump.game.game.animation.AnimationActor;
import com.fuzzjump.game.game.ingame.IngameStage;
import com.fuzzjump.game.game.ingame.actors.Player;
import com.fuzzjump.game.game.Textures;

/**
 * Kerpow Games, LLC
 * Created by stephen on 3/3/2015.
 */
public class GustSpecial extends Special {

    private Animation gustAnimation;

    @Override
    public void init() {
        super.init();
        gustAnimation = new Animation(.03f, Textures.findAnimationFrames(atlas, "wind-gust", 1, 10));
    }

    @Override
    public void activate(Player player) {
        IngameStage stage = (IngameStage) player.getStage();

        player.velocity.y += 2000;

        AnimationActor gust = new AnimationActor(gustAnimation, false);
        gust.setWidth(256);
        gust.setHeight(256);
        gust.setX(player.getX() + (player.getWidth() / 2)  - (gust.getWidth() / 2));
        gust.setY(player.getY() - gust.getHeight());
        stage.addGameActor(gust);
    }

    @Override
    public void deactivate(Player player) {

    }

    @Override
    public void onPreDraw(Player player, Batch batch, float time) {

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
        return 0;
    }
}
