package com.fuzzjump.game.game.ingame.actors;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fuzzjump.game.game.animation.Explosion;
import com.fuzzjump.game.game.ingame.IngameStage;
import com.fuzzjump.game.model.World;

/**
 * Kerpow Games, LLC
 * Created by stephen on 2/28/2015.
 */
public class Bomb extends PhysicActor {

    private final Animation explosion;

    public Bomb(World world, TextureRegion texture, Animation explosion) {
        super(world, texture);
        this.explosion = explosion;
        velocityModifier.x = 0;
    }

    @Override
    public void setDefaultVelocity() {
    }

    @Override
    public void hit(PhysicActor other) {
        if (other instanceof Player) {
            ((IngameStage)getStage()).addGameActor(new Explosion(world, getX() + getWidth() / 2, getY() + getHeight() / 2, explosion));
            remove();
        } else if (other instanceof Platform) {
            setY(other.hitbox.y + other.hitbox.height);
        }
    }

    @Override
    public boolean interested(PhysicActor other) {
        return other instanceof Player || other instanceof Platform;
    }

}
