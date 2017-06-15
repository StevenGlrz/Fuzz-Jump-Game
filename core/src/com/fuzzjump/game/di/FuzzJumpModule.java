package com.fuzzjump.game.di;

import com.fuzzjump.game.FuzzJump;
import com.fuzzjump.game.FuzzJumpParams;
import com.fuzzjump.game.game.FuzzJumpGame;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class FuzzJumpModule {

    private final FuzzJump fuzzJump;
    private final FuzzJumpParams params;

    public FuzzJumpModule(FuzzJump fuzzJump, FuzzJumpParams params) {
        this.fuzzJump = fuzzJump;
        this.params = params;
    }

    @Singleton
    @Provides
    public FuzzJump provideFuzzJump() {
        return fuzzJump;
    }

    @Singleton
    @Provides
    public FuzzJumpParams provideParams() {
        return params;
    }

}
