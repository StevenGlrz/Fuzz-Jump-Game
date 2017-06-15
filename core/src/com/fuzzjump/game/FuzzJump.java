package com.fuzzjump.game;

import com.badlogic.gdx.Game;
import com.fuzzjump.game.game.FuzzJumpGame;
import com.fuzzjump.game.game.FuzzJumpGameComponent;
import com.fuzzjump.game.game.FuzzJumpGameModule;
import com.fuzzjump.game.platform.PlatformModule;

public class FuzzJump extends Game {

    private final FuzzJumpModule fuzzJumpModule;
    private final PlatformModule platformModule;

    private FuzzJumpGame game;
    private FuzzJumpComponent component;
    private FuzzJumpGameComponent gameComponent;

    public FuzzJump(FuzzJumpParams gameParams,
                    PlatformModule platformModule) {
        this.fuzzJumpModule = new FuzzJumpModule(this, gameParams);
        this.platformModule = platformModule;
    }

    @Override
    public void create() {
        component = DaggerFuzzJumpComponent.builder()
                .fuzzJumpModule(fuzzJumpModule)
                .platformModule(platformModule)
                .build();
        gameComponent = component.gameComponent(new FuzzJumpGameModule());
        game = gameComponent.provideGame();
        game.create();
    }

    public FuzzJumpGame getFuzzJumpGame() {
        return game;
    }

    public FuzzJumpComponent getComponent() {
        return component;
    }

    public FuzzJumpGameComponent getGameComponent() {
        return gameComponent;
    }

}
