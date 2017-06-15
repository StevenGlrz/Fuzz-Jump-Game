package com.fuzzjump.game.game;

import com.fuzzjump.game.FuzzJumpComponent;
import com.fuzzjump.game.game.screen.MainScreen;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Subcomponent;

@FuzzJumpScope
@Subcomponent(
        modules = {
                FuzzJumpGameModule.class
        }
)
public interface FuzzJumpGameComponent {

    FuzzJumpGame provideGame();
    MainScreen provideMainScreen();

}
