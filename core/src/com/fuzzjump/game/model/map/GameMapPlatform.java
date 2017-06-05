package com.fuzzjump.game.model.map;

/**
 *
 * @author Steven Galarza
 */
public class GameMapPlatform {

    private int physicsY;
    private int physicsHeight;
    private String name;
	
	public GameMapPlatform(String name, int physicsY, int physicsHeight) {
		this.name = name;
        this.physicsY = physicsY;
        this.physicsHeight = physicsHeight;
	}
	
	public String getName() {
		return name;
	}

    public int getPhysicsY() {
        return physicsY;
    }

    public int getPhysicsHeight() {
        return physicsHeight;
    }
}
