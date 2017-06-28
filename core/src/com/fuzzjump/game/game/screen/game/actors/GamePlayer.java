package com.fuzzjump.game.game.screen.game.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.fuzzjump.game.game.player.Profile;
import com.fuzzjump.game.game.screen.component.ColorDrawable;
import com.fuzzjump.game.game.screen.component.Fuzzle;
import com.fuzzjump.game.game.screen.game.World;
import com.fuzzjump.libgdxscreens.screen.StageUI;

public class GamePlayer extends PhysicActor {

    public static final Color LABEL_COLOR = new Color(246f / 255f, 244f / 255f, 187f / 255f, 1f);

    private final Profile profile;
    private final Fuzzle fuzzle;
    private final ColorDrawable colorDrawable;

    private Rectangle platHitbox = new Rectangle();
    private BitmapFont font;

    private float stun;
    private int updateCounter;
    private boolean finished;
    public boolean hit;

    public GamePlayer(StageUI ui, World world, Fuzzle fuzzle, Profile profile, float x, float y, float width, BitmapFont font) {
        super(world, null);
        this.profile = profile;
        this.fuzzle = fuzzle;
        this.font = font;
        this.velocity.x = 0;

        float height = width / (fuzzle.getFuzzleDrawable().getRegion().getRegionWidth() / fuzzle.getFuzzleDrawable().getRegion().getRegionHeight());
        hitbox.setWidth(width);
        hitbox.setHeight(height);
        fuzzle.setWidth(width);
        fuzzle.setHeight(height);
        setWidth(width);
        setHeight(height);
        this.setPosition(x, y);
        colorDrawable = new ColorDrawable(Color.GREEN, width, height);
        setZIndex(5);
    }

    @Override
    public void setDefaultVelocity() {
        velocity.y = 1750;
    }

    @Override
    public void hit(PhysicActor other) {

    }

    private GlyphLayout glyphLayout = new GlyphLayout();

    @Override
    public void draw(Batch batch, float alpha) {
        //gameUI.getShader().setUniformf("radius", gameUI.getPlayer().yVel - yVel); //radius of blur
//        if (!Gdx.input.isTouched())
//            SpecialType.BALLOONS.getSpecial().onPreDraw(this, batch, 0);
        fuzzle.setX(getX());
        fuzzle.setY(getY());
        //colorDrawable.draw(batch, hitbox.x, hitbox.y, hitbox.width, hitbox.height);
        fuzzle.draw(batch, alpha);

        glyphLayout.setText(font, profile.getDisplayName());
        font.setColor(LABEL_COLOR);
        batch.setColor(LABEL_COLOR);
        font.draw(batch, glyphLayout, (getX() + getWidth() / 2) - (glyphLayout.width / 2), getY() - glyphLayout.height - 5);
        batch.setColor(Color.WHITE);
        //gameUI.getShader().setUniformf("radius", gameUI.getPlayer().yVel);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (getX() < 0) {
            setX(0);
        } else if (getX() + getWidth() > world.getWidth()) {
            setX(world.getWidth() - getWidth());
        }
        if (getY() < 0) {
            position.y = 0;
            setDefaultVelocity();
        }
        if (stun > 0) {
            stun -= delta;
        }
        if (hit) {
            setDefaultVelocity();
            hit = false;
        }
        setZIndex(Integer.MAX_VALUE);
    }

    public Rectangle getPlatformHitbox() {
        platHitbox.set(getX() + getWidth() *.15f, getY(), getWidth() * .75f, getHeight() / 4);
        return platHitbox;
    }

    public Profile getProfile() {
        return profile;
    }

    @Override
    public boolean interested(PhysicActor other) {
        return other instanceof GamePlatform;
    }

    public void stun(float time) {
        stun = time;
    }

    public float getStun() {
        return stun;
    }

    public int getUpdateCounter() {
        return updateCounter;
    }

    public void updateCounter() {
        updateCounter++;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

}
