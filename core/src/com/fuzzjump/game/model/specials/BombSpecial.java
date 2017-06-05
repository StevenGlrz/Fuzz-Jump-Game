package com.fuzzjump.game.model.specials;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.fuzzjump.game.game.ingame.IngameStage;
import com.fuzzjump.game.game.ingame.actors.Bomb;
import com.fuzzjump.game.game.ingame.actors.Player;
import com.fuzzjump.game.game.Textures;

/**
 * Kerpow Games, LLC
 * Created by stephen on 2/28/2015.
 */
public class BombSpecial extends Special {

    private Animation explosionAnimation;

    @Override
    public void init() {
        super.init();
        explosionAnimation = new Animation(.03f, Textures.findAnimationFrames(atlas, "explosion", 1, 10));
    }

    @Override
    public void activate(Player player) {
        IngameStage stage = (IngameStage) player.getStage();

        Bomb bomb = new Bomb(stage.getScreen().getWorld(), atlas.findRegion("bomb"), explosionAnimation);
        bomb.setX(player.getX() + (player.getWidth() / 2)  - (bomb.getWidth() / 2));
        bomb.setY(player.getY() - bomb.getHeight() * 1.25f);
        stage.addGameActor(bomb);
        stage.getScreen().getWorld().getPhysicsActors().add(bomb);
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
