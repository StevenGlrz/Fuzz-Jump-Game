package com.fuzzjump.game.game.screen.game.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.fuzzjump.game.game.screen.GameScreen;
import com.fuzzjump.game.game.screen.game.World;

public class CloudActor extends Image {

    private final World world;
    private boolean direction;

    public CloudActor(GameScreen screen) {
        super(screen.getUI().getTextures().getTexture("cloud"));
        this.world = screen.getWorld();
        this.direction = screen.getRandom().nextBoolean();
    }

    @Override
    public void act(float delta) {
        int move = direction ? -2 : 2;
        setPosition(getX() + move, getY());
        if (getX() + getWidth() < 0) {
            setX(world.getWidth());
        } else if (getX() > world.getWidth()) {
            setX(-getWidth());
        }
    }

}
