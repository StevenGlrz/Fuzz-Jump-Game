package com.fuzzjump.game.game.ingame.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.fuzzjump.game.game.StageUI;
import com.fuzzjump.game.game.ui.components.Fuzzle;
import com.fuzzjump.game.model.SpecialType;
import com.fuzzjump.game.model.Specials;
import com.fuzzjump.game.model.World;
import com.fuzzjump.game.model.profile.PlayerProfile;
import com.fuzzjump.game.util.ColorDrawable;

public class Player extends PhysicActor {

    public static final Color LABEL_COLOR = new Color(246f / 255f, 244f / 255f, 187f / 255f, 1f);

    private final PlayerProfile profile;
    private final Fuzzle fuzzle;
    private final ColorDrawable colorDrawable;

    private Rectangle platHitbox = new Rectangle();
    private BitmapFont font;

    private Specials specials;
    private float stun;
    private int updateCounter;
    private boolean finished;
    public boolean hit;

    public Player(StageUI ui, World world, PlayerProfile profile, float x, float y, float width, BitmapFont font) {
        super(world, null);
        this.profile = profile;
        this.fuzzle = new Fuzzle(ui, profile, false);
        this.specials = new Specials(this);
        this.font = font;
        this.velocity.x = 0;
        float height = width / (fuzzle.getFuzzleDrawable().getRegion().getRegionWidth() / fuzzle.getFuzzleDrawable().getRegion().getRegionHeight());
        System.out.println("MYHEIGHT: " + height);
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
        specials.onPreDraw(batch);
        if (!Gdx.input.isTouched())
            SpecialType.BALLOONS.getSpecial().onPreDraw(this, batch, 0);
        fuzzle.setX(getX());
        fuzzle.setY(getY());
        colorDrawable.draw(batch, hitbox.x, hitbox.y, hitbox.width, hitbox.height);
        fuzzle.draw(batch, alpha);
        specials.onPostDraw(batch);

        glyphLayout.setText(font, profile.getName());
        font.setColor(LABEL_COLOR);
        batch.setColor(LABEL_COLOR);
        font.draw(batch, glyphLayout, (getX() + getWidth() / 2) - (glyphLayout.width / 2), getY() - glyphLayout.height - 5);
        batch.setColor(Color.WHITE);
        //gameUI.getShader().setUniformf("radius", gameUI.getPlayer().yVel);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        specials.act(delta);
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
        if (!hit && !Gdx.input.isTouched())
            velocity.y = 1250;
        hit = false;
        setZIndex(Integer.MAX_VALUE);
    }

    public Specials getSpecials() {
        return specials;
    }

    public Rectangle getPlatformHitbox() {
        platHitbox.set(getX() + getWidth() *.15f, getY(), getWidth() * .75f, getHeight() / 4);
        return platHitbox;
    }

    public PlayerProfile getProfile() {
        return profile;
    }

    @Override
    public boolean interested(PhysicActor other) {
        return other instanceof Platform;
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
