package com.fuzzjump.game.game.screen.component;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.fuzzjump.libgdxscreens.Textures;
import com.fuzzjump.libgdxscreens.screen.StageUI;

import java.util.Random;

public class SnowActor extends Actor {

    public static final float SNOW_VELOCITY = 1f;

    private final float maxWidth;
    private final float maxHeight;
    private final Array<Snow> snowflakes = new Array<>();
    private final Textures textures;
    private final Random random = new Random();
    private final TextureRegion[] regions = new TextureRegion[4];
    private float velocity;
    private float velocityModifier;

    public SnowActor(StageUI ui) {
        this.textures = ui.getTextures().getTextures();

        this.maxWidth = Gdx.graphics.getWidth();
        this.maxHeight = Gdx.graphics.getHeight();
        this.velocityModifier = 200f * (maxHeight / 1800);

        this.velocity = SNOW_VELOCITY;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (snowflakes.size == 0) {
            addRandomFlake(30);
        }
        velocity = -(velocity / velocityModifier);
        for (int i = 0, n = snowflakes.size; i < n; i++) {
            Snow snow = snowflakes.get(i);

            TextureRegion texture = regions[snow.type];

            batch.draw(texture, snow.x, snow.y, .5f, .5f, (float) texture.getRegionWidth(), (float) texture.getRegionHeight(), 1f, 1f, snow.currentRot += snow.rot);

            snow.y += (snow.velocity + velocity);
            if (velocity <= 0 && snow.y + texture.getRegionHeight() < 0) {
                snow.x = texture.getRegionWidth() / 2 + random.nextInt((int) (maxWidth - texture.getRegionWidth() * 1.5));
                snow.y = maxHeight + texture.getRegionHeight();
            } else if (velocity > 0 && snow.y + texture.getRegionHeight() / 2 > maxHeight) {
                snow.x = texture.getRegionWidth() / 2 + random.nextInt((int) (maxWidth - texture.getRegionWidth() * 1.5));
                snow.y = -texture.getRegionHeight();
            }
        }
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    private void addRandomFlake(int flakes) {
        for (int i = 0; i < flakes; i++) {
            Snow snow = new Snow();

            snow.type = random.nextInt(4);
            TextureRegion texture = regions[snow.type];
            if (texture == null) {
                texture = regions[snow.type] = textures.getTextureIgnoreMissing("snowflake" + (snow.type + 1));
            }
            //not loaded yet.
            if (texture == null) {
                return;
            }

            snow.x = random.nextInt((int) (maxWidth - texture.getRegionWidth()));
            snow.y = maxHeight - random.nextInt((int) maxHeight);
            snow.velocity = random.nextFloat() + (-(2 + random.nextInt(4))) * ((float) Gdx.graphics.getHeight() / 1800);
            snow.rot = -1f + random.nextFloat() + random.nextInt(2);
            snowflakes.add(snow);
        }
    }

    private class Snow {
        private int type;
        private float x;
        private float y;
        private float velocity;
        private float currentRot;
        private float rot;
    }
}
