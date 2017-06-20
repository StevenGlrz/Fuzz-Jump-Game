package com.fuzzjump.game.game.screen.game.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.fuzzjump.game.game.screen.component.ColorDrawable;
import com.fuzzjump.game.game.screen.game.World;

public class GamePlatform extends PhysicActor {

    public static final float DELTA_X = 750;
    private final ColorDrawable colorDrawable;

    protected TextureRegion texture;
	protected float drawHeight; //account for fuzz's
	private boolean winner;

	public GamePlatform(World world, float x, float y, float width, float height, TextureRegion texture) {
        super(world, texture);
		this.texture = texture;
		this.hitbox = new Rectangle(x, y, width, height * .85f);
		this.drawHeight = height;
        this.world = world;
		this.setPosition(x, y);
        this.setZIndex(0);
        this.velocityModifier.set(0, 0);
        colorDrawable = new ColorDrawable(Color.GREEN, width, height);
	}

	public GamePlatform(World world, float x, float y, float width, float height, float physicsY, float drawHeight, TextureRegion texture) {
		this(world, x, y, width, height, texture);
		this.drawHeight = drawHeight;
        this.hitbox.height = physicsY;
	}

    @Override
	public void hit(PhysicActor other) {
        if (other instanceof GamePlayer) {
            ((GamePlayer) other).velocity.y = 0;
            ((GamePlayer) other).hit = true;
        }
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		//	batch.begin();
		//batch.setColor(getColor());
        colorDrawable.draw(batch, hitbox.x, hitbox.y, hitbox.width, hitbox.height);
		if (texture != null)
			batch.draw(texture, getX(), getY(), hitbox.getWidth(), drawHeight);
		//batch.setColor(Color.WHITE);
		//	batch.end();
	}

    @Override
    public void act(float delta) { // || getX() + rectangle.width > world.getWidth()
        if (velocityModifier.x == 0 && velocity.x > 0) velocityModifier.x = 1;
        if (getX() <= 0) {
            setX(Math.abs(getX()));
            velocity.x *= -1;
        } else if (getX() + getWidth() >= world.getWidth()) {
            float overflow = getX() + getWidth() - world.getWidth();
            setX(world.getWidth() - getWidth() - overflow);
            velocity.x *= -1;
        }
    }

    @Override
    public void setDefaultVelocity() {
        velocity.x = DELTA_X;
    }

    @Override
    public boolean interested(PhysicActor other) {
        return false;
    }

	public void setWinner(boolean winner) {
		this.winner = winner;
	}

	public boolean isWinner() {
		return winner;
	}

}
