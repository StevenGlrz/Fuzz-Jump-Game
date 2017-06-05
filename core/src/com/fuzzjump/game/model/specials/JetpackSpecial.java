package com.fuzzjump.game.model.specials;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.fuzzjump.game.game.ingame.IngameStage;
import com.fuzzjump.game.game.animation.AnimationActor;
import com.fuzzjump.game.game.ingame.actors.Player;
import com.fuzzjump.game.game.Textures;

/**
 * Created by Steven on 2/2/2015.
 */
public class JetpackSpecial extends Special {

    public static final float PARTICLE_DELTA_X = 150;

    private Animation jetpackExhaustAnimation;
    private Animation jetpackAnimation;

    @Override
    public void init() {
        super.init();
        jetpackAnimation = new Animation(.05f, atlas.findRegion("item-jetpack-frame1"), atlas.findRegion("item-jetpack-frame2"));
        jetpackAnimation.setPlayMode(Animation.PlayMode.LOOP);
        jetpackExhaustAnimation = new Animation(.021f, Textures.findAnimationFrames(atlas, "jetpack-exhaust-sprite", 1, 48));
    }

    @Override
    public void dispose() {
        super.dispose();
        jetpackAnimation = null;
        jetpackExhaustAnimation = null;
    }

    @Override
    public void activate(Player player) {

    }

    @Override
    public void deactivate(Player player) {

    }

    @Override
    public void onPreDraw(Player player, Batch batch, float frameTime) {
        TextureRegion region = jetpackAnimation.getKeyFrame(frameTime, true);
        batch.draw(region, (player.getX() + player.getWidth() / 2) - region.getRegionWidth() / 2, (player.getY() + player.getHeight() / 2) - region.getRegionHeight() / 2);
    }

    @Override
    public void onPostDraw(Player player, Batch batch, float frameTime) {
        TextureRegion region = atlas.findRegion("item-jetpack-straps");
        batch.draw(region, (player.getX() + player.getWidth() / 2) - region.getRegionWidth() / 2, (player.getY() + player.getHeight() / 2) - region.getRegionHeight() / 2);
    }

    @Override
    public void act(IngameStage stage, Player player, float frameTime, boolean addParticle) {
         player.velocity.y = 2000;

        if (addParticle) {
            addParticle(stage, player, -10);
            addParticle(stage, player, -80);
        }
    }

    public void addParticle(IngameStage stage, Player player, float xOff) {
        AnimationActor particle = new AnimationActor(jetpackExhaustAnimation);
        particle.setPosition((player.getX() + player.getWidth() / 2) + xOff, (player.getY() + player.getHeight() / 2) - 150);
        particle.setWidth(96);
        particle.setHeight(96);
        particle.setXVel(xOff > -50 ? PARTICLE_DELTA_X : -PARTICLE_DELTA_X);
        stage.addGameActor(particle);
    }
    @Override
    public void onInputEvent(Player player, InputEvent event) {

    }

    @Override
    public long getDuration() {
        return 1000;
    }

}
