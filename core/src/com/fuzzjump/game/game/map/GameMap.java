package com.fuzzjump.game.game.map;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Steven Galarza
 */
public class GameMap {

    public final static String[] MAPS = {
            "sunny_day_map",
            "snow_map",
            "jungle_map",
            "city_map",
			"desert_map"
    };

	public static final int PLATFORM_GAP = 475; // TODO Maybe this should change between maps

	private List<GameMapPlatform> platforms = new LinkedList<GameMapPlatform>();
	private List<GameMapBackground> background = new LinkedList<GameMapBackground>();

	private boolean clouds, snow;

	private int width;
	private int height;
	
	private GameMapGround ground;

	public List<GameMapBackground> getBackgrounds() {
		return background;
	}

	public boolean hasClouds() {
		return clouds;
	}

    public boolean snowing() { return snow; }

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void addBackgroundLayer(GameMapBackground layer) {
		background.add(layer);
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setClouds(boolean clouds) {
		this.clouds = clouds;
	}

	public boolean isCloudEnabled() {
		return clouds;
	}

	public void addPlatform(GameMapPlatform platform) {
		platforms.add(platform);
	}

	// Methods from world builder
	
	public void setGround(GameMapGround ground) {
		this.ground = ground;
	}
	
	public GameMapGround getGround() {
		return ground;
	}

    public void setSnowing(boolean snowing) {
        this.snow = snowing;
    }

	public List<GameMapPlatform> getPlatforms() {
		return platforms;
	}
}
