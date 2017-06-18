package com.fuzzjump.game.game.screen.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;

public class ColorDrawable extends BaseDrawable {

	private final Color color;
	private final float setWidth;
	private final float setHeight;
	private Texture texture;

	public ColorDrawable(Color color, float setWidth, float setHeight) {
		this.color = color;
		this.setWidth = setWidth;
		this.setHeight = setHeight;
	}

	@Override
	public void draw(Batch batch, float x, float y, float width, float height) {
		if (texture == null) {
			Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
			pixmap.setColor(Color.WHITE);
			pixmap.fill();

			texture = new Texture(pixmap);

			this.setMinWidth(this.setWidth);
			this.setMinHeight(this.setHeight);
		}
		Color before = batch.getColor();
		batch.setColor(color);
		batch.draw(texture, x, y, width, height);
		batch.setColor(before);
	}
	
}
