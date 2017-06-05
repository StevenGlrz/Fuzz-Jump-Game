package com.fuzzjump.game.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Disposable;
import com.fuzzjump.game.game.ingame.IngameStage;
import com.fuzzjump.game.game.ingame.actors.Player;
import com.fuzzjump.game.model.specials.ShieldSpecial;

public class Specials implements Disposable {

    public static final float SPECIAL_DELAY = 1.5f;
    public static final float PARTICLE_DELAY = .025f;

    private Player player;
    private SpecialType currentSpecial;

    private long specialStarted = -1;
    private float specialDelay = 0;
    private float frameTime = 0;

    private float shieldTime = 0f;
    private float nextParticle = PARTICLE_DELAY;
    private boolean shieldOn;

    public Specials(Player player) {
        this.player = player;
        ((InputMultiplexer) Gdx.input.getInputProcessor()).addProcessor(new InputAdapter() {

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                activateSpecial();
                return true;
            }

        });
    }

    //load/unload methods
    public void initSpecials() {
        for (SpecialType type : SpecialType.values()) {
            type.spec.init();
        }
    }

    public void dispose() {
        currentSpecial = null;
        for (SpecialType type : SpecialType.values()) {
            type.spec.dispose();
        }
    }

    //logic
    public void perform(SpecialType special, boolean delay) {
        specialDelay = delay ? SPECIAL_DELAY : 0;
        specialStarted = -1;
        frameTime = 0;
        nextParticle = PARTICLE_DELAY;
        currentSpecial = special;
    }

    public void act(float delta) {
        if (currentSpecial != null) {

            if (specialDelay > 0) {
                if (specialDelay - delta < .001)
                    specialDelay = .001f; //stops it from performing until tap
                else
                    specialDelay -= delta;
                return;
            } else if (specialStarted == -1) {
                specialStarted = System.currentTimeMillis();
                currentSpecial.spec.activate(player);
                specialDelay = 0;
            }

            boolean particle = false;
            if (frameTime > nextParticle) {
                particle = true;
                nextParticle += PARTICLE_DELAY;
            }
            currentSpecial.spec.act((IngameStage) player.getStage(), player, frameTime, particle);
            if (specialStarted + currentSpecial.spec.getDuration() < System.currentTimeMillis()) {
                currentSpecial.spec.deactivate(player);
                currentSpecial = null;
                specialStarted = -1;
            }
            frameTime += delta;

            //we need an animation system..
            if (shieldOn) {
                if (((ShieldSpecial)SpecialType.SHIELD.getSpecial()).getShieldAnimation().isAnimationFinished(shieldTime)) {
                    shieldOn = false;
                    shieldTime = 0;
                    return;
                }
                shieldTime += delta;
            }

        }
    }

    public void onPreDraw(Batch batch) {
        if (currentSpecial != null && specialDelay == 0) {
            currentSpecial.spec.onPreDraw(player, batch, frameTime);
        }
    }

    public void onPostDraw(Batch batch) {
        if (currentSpecial != null && specialDelay == 0) {
            currentSpecial.spec.onPostDraw(player, batch, frameTime);
            if (shieldOn) {
                TextureRegion frame = ((ShieldSpecial)SpecialType.SHIELD.getSpecial()).getShieldAnimation().getKeyFrame(shieldTime);
                batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, ShieldSpecial.SHIELD_ALPHA);
                batch.draw(frame, (player.getX() + player.getWidth() / 2) - frame.getRegionWidth() / 2, (player.getY() + player.getHeight() / 2) - frame.getRegionHeight() / 2);
                batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, 1f);

            }
        }
    }

    public void onInputEvent(InputEvent event) {
        if (currentSpecial != null) {
            currentSpecial.spec.onInputEvent(player, event);
        }
    }
    public void activateSpecial() {
        if (currentSpecial != null && specialDelay == .001f)
            specialDelay = 0;
    }

    public float getCurrentDelay() {
        return specialDelay;
    }

    public SpecialType getCurrentSpecial() {
        return currentSpecial;
    }

    public void playShieldAnim() {
        shieldOn = true;
    }
}
