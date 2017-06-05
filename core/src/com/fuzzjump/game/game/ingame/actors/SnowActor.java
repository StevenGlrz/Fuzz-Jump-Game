package com.fuzzjump.game.game.ingame.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.fuzzjump.game.FuzzJump;
import com.fuzzjump.game.game.Textures;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SnowActor {

	private float maxWidth;
	private float maxHeight;
	private List<Snow> snowflakes = new ArrayList<Snow>();
	private Textures textures;
	private Random random = new Random();

	public SnowActor(Textures textures, float maxWidth, float maxHeight) {
		this.textures = textures;
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
	}

	public void draw(Batch batch, float parentAlpha, float velocity) {
		if (snowflakes.size() == 0) {
			addRandomFlake(30);
		}
        velocity = -(velocity / (200f * ((float)Gdx.graphics.getHeight() / 1800)));
		//long time = System.currentTimeMillis();
		batch.begin();
		for (int i = 0, n = snowflakes.size(); i < n; i++) {
			Snow snow = snowflakes.get(i);

			TextureRegion texture = snowregions[snow.type];

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
		batch.end();
		//Log.i("steveadoo", "snowtime: " + (System.currentTimeMillis() - time));
	}

	TextureRegion[] snowregions = new TextureRegion[4];

	public void addRandomFlake(int flakes) {
		Snow snow = new Snow();

		snow.type = random.nextInt(4);
		TextureRegion texture = snowregions[snow.type];
		if (texture == null)
			texture = snowregions[snow.type] = textures.getTexture("snowflake" + (snow.type + 1));

		snow.x = random.nextInt((int) (maxWidth - texture.getRegionWidth()));
		snow.y = maxHeight - random.nextInt((int) maxHeight);
		snow.velocity = random.nextFloat() + (-(2 + random.nextInt(4))) * ((float)Gdx.graphics.getHeight() / 1800);
		snow.rot = -1f + random.nextFloat() + random.nextInt(2);
		snowflakes.add(snow);

		if (flakes > 0) addRandomFlake(--flakes);
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
