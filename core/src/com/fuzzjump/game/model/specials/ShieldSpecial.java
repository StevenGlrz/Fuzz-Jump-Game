package com.fuzzjump.game.model.specials;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.fuzzjump.game.game.ingame.IngameStage;
import com.fuzzjump.game.game.ingame.actors.Player;
import com.fuzzjump.game.game.Textures;

/**
 * Kerpow Games, LLC
 * Created by stephen on 2/21/2015.
 */
public class ShieldSpecial extends Special {

    public static final float SHIELD_ALPHA = .75f;

    private TextureRegion shieldTexture;
    private Animation shieldAnimation;

    @Override
    public void init() {
        super.init();
        shieldTexture = atlas.findRegion("shield0001");
        shieldAnimation = new Animation(.1f, Textures.findAnimationFrames(atlas, "shield", 2, 6));
    }

    @Override
    public void activate(Player player) {
    }

    @Override
    public void deactivate(Player player) {
    }

    @Override
    public void onPreDraw(Player player, Batch batch, float frameTime) {

    }

    @Override
    public void onPostDraw(Player player, Batch batch, float frameTime) {
        batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, SHIELD_ALPHA);
        batch.draw(shieldTexture, (player.getX() + player.getWidth() / 2) - shieldTexture.getRegionWidth() / 2, (player.getY() + player.getHeight() / 2) - shieldTexture.getRegionHeight() / 2);
        batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, 1f);
    }

    @Override
    public void act(IngameStage stage, Player player, float delta, boolean addParticle) {

    }

    @Override
    public void onInputEvent(Player player, InputEvent event) {

    }

    @Override
    public long getDuration() {
        return 10000;
    }

    @Override
    public void dispose() {

    }

    public Animation getShieldAnimation() {
        return shieldAnimation;
    }
}
