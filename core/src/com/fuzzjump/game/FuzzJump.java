package com.fuzzjump.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.fuzzjump.api.RetrofitModule;
import com.fuzzjump.game.di.DaggerFuzzJumpComponent;
import com.fuzzjump.game.di.FuzzJumpModule;
import com.fuzzjump.game.di.FuzzJumpComponent;
import com.fuzzjump.game.game.FuzzJumpGame;
import com.fuzzjump.game.game.di.FuzzJumpGameComponent;
import com.fuzzjump.game.game.di.FuzzJumpGameModule;
import com.fuzzjump.game.platform.PlatformModule;

public class FuzzJump extends Game {

    private final FuzzJumpModule fuzzJumpModule;
    private final RetrofitModule retrofitModule;
    private final PlatformModule platformModule;

    private FuzzJumpGame game;
    private FuzzJumpComponent component;
    private FuzzJumpGameComponent gameComponent;

    public FuzzJump(FuzzJumpParams gameParams,
                    PlatformModule platformModule) {
        this.fuzzJumpModule = new FuzzJumpModule(this, gameParams);
        this.retrofitModule = new RetrofitModule(gameParams.apiUrl, "./");
        this.platformModule = platformModule;
    }

    @Override
    public void create() {
        component = DaggerFuzzJumpComponent.builder()
                .fuzzJumpModule(fuzzJumpModule)
                .platformModule(platformModule)
                .retrofitModule(retrofitModule)
                .build();
        gameComponent = component.gameComponent(new FuzzJumpGameModule());
        game = gameComponent.provideGame();
        game.create();
    }

    public FuzzJumpComponent getComponent() {
        return component;
    }

    public FuzzJumpGameComponent getGameComponent() {
        return gameComponent;
    }

}
