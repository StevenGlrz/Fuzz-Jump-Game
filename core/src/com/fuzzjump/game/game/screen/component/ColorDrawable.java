package com.fuzzjump.game.game.screen.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;

public class ColorDrawable extends BaseDrawable {

	private final Color color;
	private final float width;
	private final float height;
	private Texture texture;

	public ColorDrawable(Color color, float width, float height) {
		this.color = color;
		this.width = width;
		this.height = height;
	}

	@Override
	public void draw(Batch batch, float x, float y, float width, float height) {
		if (texture == null) {
			Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
			pixmap.setColor(Color.WHITE);
			pixmap.fill();

			texture = new Texture(pixmap);

			this.setMinWidth(this.width);
			this.setMinHeight(this.height);
		}
		Color before = batch.getColor();
		batch.setColor(color);
		batch.draw(texture, x, y, width, height);
		batch.setColor(before);
	}
	
}
