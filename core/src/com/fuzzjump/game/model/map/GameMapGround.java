package com.fuzzjump.game.model.map;

/**
 *
 * @author Steven Galarza
 */
public class GameMapGround {

	private String name;
	private float y;
	private float height;
	private float realHeight;
	private String fillTexture;
	
	public GameMapGround(String name, float y, float height, float realHeight) {
		this.name = name;
		this.y = y;
		this.height = height;
		this.realHeight = realHeight;
	}
	
	public String getName() {
		return name;
	}
	
	public float getY() {
		return y;
	}
	
	public float getHeight() {
		return height;
	}
	
	public float getRealHeight() {
		return realHeight;
	}

}