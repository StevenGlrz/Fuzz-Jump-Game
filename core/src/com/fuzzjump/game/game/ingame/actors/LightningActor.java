package com.fuzzjump.game.game.ingame.actors;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.fuzzjump.game.game.animation.AnimationActor;
import com.fuzzjump.game.game.ingame.IngameStage;
import com.fuzzjump.game.model.SpecialType;

/**
 * Kerpow Games, LLC
 * Created by stephen on 4/6/2015.
 */
public class LightningActor extends AnimationActor {

    private final Player target;

    public LightningActor(Player target, Animation lightningAnimation) {
        super(lightningAnimation, false);
        this.target = target;
        setWidth(lightningAnimation.getKeyFrames()[lightningAnimation.getKeyFrames().length - 1].getRegionWidth());
        setHeight(1280);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        IngameStage stage = (IngameStage) getStage();
        float playerY = stage.getScreen().getPlayer().getY();
        float targetY = target.getY() + target.getHeight();
        if (Math.abs(playerY - targetY) <= 1280) {
            setY(targetY + 1280);
            super.draw(batch, parentAlpha);
        }
    }
    @Override
    public void act(float delta) {
        super.act(delta);
        if (animation.isAnimationFinished(frameTime)) {
            if (target.getSpecials().getCurrentSpecial() == SpecialType.SHIELD) {
                target.getSpecials().playShieldAnim();
            } else {
                target.stun(7f);
            }
        }
    }
}
