package com.fuzzjump.game.game.ingame.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.fuzzjump.game.model.World;

/**
 * Kerpow Games, LLC
 * Created by stephen on 2/28/2015.
 */
public abstract class PhysicActor extends Image {

    protected World world;
    public Vector2 previousPosition = new Vector2();
    public Vector2 position = new Vector2();
    public Vector2 velocity = new Vector2();
    public Vector2 velocityModifier = new Vector2();
    public boolean removed = false;
    public Rectangle hitbox = new Rectangle() {

        @Override
        public float getY() {
            return position.y;
        }

        @Override
        public float getX() {
            return position.x;
        }

        @Override
        public float getWidth() {
            return PhysicActor.this.getWidth();
        }

        @Override
        public float getHeight() {
            return PhysicActor.this.getHeight();
        }

    };

    public PhysicActor(World world, TextureRegion texture) {
        this.world = world;
        velocityModifier.set(1, 1);
        if (texture != null) {
            setSize(texture.getRegionWidth(), texture.getRegionHeight());
            setDrawable(new TextureRegionDrawable(texture));
        }
    }

    public abstract void setDefaultVelocity();
    public abstract void hit(PhysicActor other);
    public abstract boolean interested(PhysicActor other);

    @Override
    public void setPosition(float x, float y) {
        setX(x);
        setY(y);
    }

    @Override
    public float getY() {
        return position.y + getOriginY();
    }

    @Override
    public float getX() {
        return position.x + getOriginX();
    }

    @Override
    public float getX(int align) {
        return position.x;
    }

    @Override
    public float getY(int align) {
        return position.y;
    }

    public void setWidth(float width) {
        if (this.hitbox != null)
            this.hitbox.width = width;
        super.setWidth(width);
    }

    public void setHeight(float height) {
        if (this.hitbox != null)
            this.hitbox.height = height;
        super.setHeight(height);
    }

    @Override
    public void setSize(float width, float height) {
        setWidth(width);
        setHeight(height);
    }

    @Override
    public void setY(float y) {
        position.y = y;
        hitbox.setY(y);
    }

    @Override
    public void setX(float x) {
        position.x = x;
        hitbox.setX(x);
    }

    @Override
    public boolean remove() {
        removed = super.remove();
        return removed;
    }

}
