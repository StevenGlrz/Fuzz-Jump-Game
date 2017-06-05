package com.fuzzjump.game.game.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * We might want to implement some pool for these, but its not a big deal
 * Kerpow Games, LLC
 * Created by stephen on 2/21/2015.
 */
public class AnimationActor extends Actor {

    protected final Animation animation;
    private boolean fade = true;

    protected float frameTime = 0;
    private float xVel = 0;
    private float yVel = 0;

    public AnimationActor(Animation animation) {
        this.animation = animation;
    }

    public AnimationActor(Animation animation, boolean fade) {
        this(animation);
        this.fade = fade;
    }

    @Override
    public void draw(Batch batch, float alpha) {
        super.draw(batch, alpha);
        if (!animation.isAnimationFinished(frameTime)) {
            if (fade) {
                batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, 1f - (1f * (frameTime / animation.getAnimationDuration())));
            }
            TextureRegion frame = animation.getKeyFrame(frameTime);
            batch.draw(frame, getX(), getY(), getWidth(), getHeight());
            batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, 1f);
        }
    }

    @Override
    public void act(float delta) {
        if (animation.isAnimationFinished(frameTime)) {
            remove();
            return;
        }
        frameTime += delta;

        setX(getX() + (delta * xVel));
        setY(getY() + (delta * yVel));
    }

    public void setXVel(float xVel) {
        this.xVel = xVel;
    }

    public void setYVel(float yVel) {
        this.yVel = yVel;
    }
}
