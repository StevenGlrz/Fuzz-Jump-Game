package com.fuzzjump.game;

import com.badlogic.gdx.Game;
import com.fuzzjump.game.di.FuzzJumpModule;
import com.fuzzjump.game.game.FuzzJumpGame;
import com.fuzzjump.game.game.di.FuzzJumpGameComponent;
import com.fuzzjump.game.game.di.FuzzJumpGameModule;
import com.fuzzjump.game.platform.PlatformModule;

public class FuzzJump extends Game {

    private final com.fuzzjump.game.di.FuzzJumpModule fuzzJumpModule;
    private final PlatformModule platformModule;

    private FuzzJumpGame game;
    private com.fuzzjump.game.di.FuzzJumpComponent component;
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

    public com.fuzzjump.game.di.FuzzJumpComponent getComponent() {
        return component;
    }

    public FuzzJumpGameComponent getGameComponent() {
        return gameComponent;
    }

}
