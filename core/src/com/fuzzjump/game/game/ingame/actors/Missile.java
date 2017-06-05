package com.fuzzjump.game.game.ingame.actors;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.fuzzjump.game.game.animation.AnimationActor;
import com.fuzzjump.game.game.animation.Explosion;
import com.fuzzjump.game.game.ingame.IngameStage;
import com.fuzzjump.game.model.World;

/**
 * Kerpow Games, LLC
 * Created by stephen on 2/28/2015.
 */
public class Missile extends PhysicActor {

    public static final float SPEED = 1500;

    private final Animation missileAnimation;
    private final Animation explosionAnimation;
    private final Animation exhaustAnimation;
    private final Player target;

    private float frameTime = 0, nextParticle = .015f;

    public Missile(World world, Player chosen, Animation missileAnimation, Animation explosionAnimation, Animation jetpackExhaustAnimation) {
        super(world, missileAnimation.getKeyFrames()[0]);
        this.missileAnimation = missileAnimation;
        this.explosionAnimation = explosionAnimation;
        this.exhaustAnimation = jetpackExhaustAnimation;
        this.target = chosen;
        setRotation(0);
        setOrigin(getWidth() / 2, getHeight() / 2);
    }

    @Override
    public void setDefaultVelocity() {

    }

    @Override
    public void hit(PhysicActor other) {

    }

    public void draw(Batch batch, float parentAlpha) {
        batch.draw(missileAnimation.getKeyFrame(frameTime), getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), 1f, 1f, getRotation());
    }

    static final float ease = 5F;

    @Override
    public void act(float delta) {
        //check hit
        float angle = getRotation() - 90;
        double radians = Math.toRadians(angle);
        float hitboxX = getX() - ((float) Math.cos(radians) * (getOriginY() + 48));
        float hitboxY = getY() + (getOriginY() - 48 - (float) Math.sin(radians) * (getOriginY() + 48));
        if (target.hitbox.contains(hitboxX, hitboxY)) {
            ((IngameStage)getStage()).addGameActor(new Explosion(world, hitboxX, hitboxY, explosionAnimation));
            remove();
        }
        //add particle
        if (nextParticle < 0) {
            angle = getRotation() + 90;
            radians = Math.toRadians(angle);
            float offX = (float) Math.cos(radians) * (getOriginY() + 48);
            float offY = (float) Math.sin(radians) * (getOriginY() + 48);

            AnimationActor particle = new AnimationActor(exhaustAnimation);
            particle.setPosition(getX() - offX, getY() + (getOriginY() - 48 - offY));
            particle.setWidth(96);
            particle.setHeight(96);
            world.getScreen().getIngameStage().addGameActor(particle);
            nextParticle = .015f + delta;
        }
        //set velocity and rotation
        float rotation = (float) (Math.atan2((target.position.y + target.getHeight() / 2) - position.y, (target.position.x + target.getWidth() / 2) - position.x) * 180 / Math.PI) - 90;
        //if (rotation < getRotation())
       //     setRotation(getRotation() - Math.abs(getRotation() - rotation) / ease);
       // else
       ///     setRotation(getRotation() + Math.abs(rotation - getRotation()) / ease);
        setRotation(rotation);
        float rad = (float) Math.toRadians(getRotation() + 90);
        velocity.set((float) (Math.cos(rad) * SPEED), (float) (Math.sin(rad) * SPEED));
        frameTime += delta;
        nextParticle -= delta;
    }

    @Override
    public boolean interested(PhysicActor other) {
        return false;
    }
}
