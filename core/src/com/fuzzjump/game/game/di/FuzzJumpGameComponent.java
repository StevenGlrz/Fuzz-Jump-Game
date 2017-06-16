package com.fuzzjump.game.game.di;

import com.fuzzjump.game.game.FuzzJumpGame;
<<<<<<< HEAD
=======
import com.fuzzjump.game.game.screen.MainScreen;
>>>>>>> 5cc2b50604995c8a81d9083877305124f581a48c
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
<<<<<<< HEAD
    SplashScreen provideMainScreen();
=======
    MainScreen provideMainScreen();
    SplashScreen provideSplashScreen();
>>>>>>> 5cc2b50604995c8a81d9083877305124f581a48c

}
