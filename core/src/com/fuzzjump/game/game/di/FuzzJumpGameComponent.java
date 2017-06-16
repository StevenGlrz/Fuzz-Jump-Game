package com.fuzzjump.game.game.di;

import com.fuzzjump.game.game.FuzzJumpGame;
import com.fuzzjump.game.game.screen.SplashScreen;

import dagger.Subcomponent;

@FuzzJumpScope
@Subcomponent(
        modules = {
                FuzzJumpGameModule.class
        }
)
public interface FuzzJumpGameComponent {

    FuzzJumpGame provideGame();
    SplashScreen provideMainScreen();

}
