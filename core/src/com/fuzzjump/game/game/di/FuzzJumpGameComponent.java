package com.fuzzjump.game.game.di;

import com.fuzzjump.game.game.FuzzJumpGame;
import com.fuzzjump.game.game.screen.MainScreen;
import com.fuzzjump.game.game.screen.MenuScreen;
import com.fuzzjump.game.game.screen.SplashScreen;
import com.fuzzjump.game.game.screen.WaitingScreen;

import dagger.Subcomponent;

@FuzzJumpScope
@Subcomponent(
        modules = {
                FuzzJumpGameModule.class
        }
)
public interface FuzzJumpGameComponent {

    FuzzJumpGame provideGame();
    SplashScreen provideSplashScreen();
    MainScreen provideMainScreen();
    MenuScreen provideMenuScreen();
    WaitingScreen provideWaitingScreen();
}
