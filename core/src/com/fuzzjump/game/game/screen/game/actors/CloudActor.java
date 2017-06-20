package com.fuzzjump.game.game.ingame.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.fuzzjump.game.game.screens.GameScreen;

public class CloudActor extends Image {

    private final GameScreen screen;
    private boolean direction;
	
	public CloudActor(GameScreen screen) {
		super(screen.ui().getTexture("cloud"));
		this.screen = screen;
		this.direction = screen.getRandom().nextBoolean();
	}
	
	@Override
	public void act(float delta) {
		int move = direction ? -2 : 2;
		setPosition(getX() + move, getY());
		if (getX() + getWidth() < 0)
			setX(screen.getWorld().getWidth());
		else if (getX() > screen.getWorld().getWidth())
			setX(-getWidth());
	}

}
