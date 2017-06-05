package com.fuzzjump.game.game.ingame.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.fuzzjump.game.model.SpecialType;
import com.fuzzjump.game.model.World;

/**
 * Kerpow Games, LLC
 * Created by stephen on 2/22/2015.
 */
public class SpecialBox extends PhysicActor {

    private final SpecialType specialType;

    public SpecialBox(World world, float x, float y, float width, float height, SpecialType type, TextureRegion region) {
        super(world, region);
        this.hitbox = new Rectangle(x, y, width, height);
        this.specialType = type;
        this.setPosition(x, y);
        this.setZIndex(5);
        velocityModifier.set(0, 0);
    }

    @Override
    public void act(float delta) {
        setZIndex(Integer.MAX_VALUE - 1);
    }

    @Override
    public void setDefaultVelocity() {
        velocity.x = 0;
        velocity.y = 0;
    }

    @Override
    public void hit(PhysicActor other) {
        if (((Player) other).getSpecials().getCurrentSpecial() == null) {
            ((Player) other).getSpecials().perform(specialType, true);
            remove();
        }
    }

    @Override
    public boolean interested(PhysicActor other) {
        return other instanceof Player;
    }

}
