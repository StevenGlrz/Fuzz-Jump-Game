package com.fuzzjump.game.model.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 *
 * @author Steven Galarza
 */
public class GameMapBackground {

	private String name;
	private Vector2 position;
	private Vector2 bounds;
	private Vector2 parallax;
	private TextureRegion texture;
	private float yOffset;
	private float layerOffset;
	
	public GameMapBackground(String name, Vector2 position, Vector2 bounds, Vector2 parallax, float yOffset, float layerOffset) {
		this.name = name;
		this.position = position;
		this.bounds = bounds;
		this.parallax = parallax;
		this.yOffset = yOffset;
		this.layerOffset = layerOffset;
	}
	
	public void setTexture(TextureRegion texture) {
		this.texture = texture;
	}

	public String getName() {
		return name;
	}
	
	public Vector2 getPosition() {
		return position;
	}

	public Vector2 getBounds() {
		return bounds;
	}

	public Vector2 getParallax() {
		return parallax;
	}

	public TextureRegion getTexture() {
		return texture;
	}
	
	public float getYOffset() {
		return yOffset;
	}

	public float getLayerOffset() {
		return layerOffset;
	}
	
}
