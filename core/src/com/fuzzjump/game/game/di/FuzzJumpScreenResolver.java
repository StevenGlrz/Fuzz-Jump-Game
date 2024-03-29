package com.fuzzjump.game.game.di;

import com.fuzzjump.game.FuzzJump;
import com.fuzzjump.game.game.screen.GameScreen;
import com.fuzzjump.game.game.screen.MainScreen;
import com.fuzzjump.game.game.screen.MenuScreen;
import com.fuzzjump.game.game.screen.SplashScreen;
import com.fuzzjump.game.game.screen.WaitingScreen;
import com.fuzzjump.libgdxscreens.screen.ScreenResolver;
import com.fuzzjump.libgdxscreens.screen.StageScreen;

public class FuzzJumpScreenResolver implements ScreenResolver {

    private final FuzzJump fuzzJump;

    public FuzzJumpScreenResolver(FuzzJump fuzzJump) {
        this.fuzzJump = fuzzJump;
    }

    @Override
    public <T extends StageScreen> T resolveScreen(Class<T> screenClazz) {
        if (screenClazz == SplashScreen.class) {
            return (T) fuzzJump.getGameComponent().provideSplashScreen();
        } else if (screenClazz == MainScreen.class) {
            return (T) fuzzJump.getGameComponent().provideMainScreen();
        } else if (screenClazz == MenuScreen.class) {
            return (T) fuzzJump.getGameComponent().provideMenuScreen();
        } else if (screenClazz == WaitingScreen.class) {
            return (T) fuzzJump.getGameComponent().provideWaitingScreen();
        } else if (screenClazz == GameScreen.class) {
            return (T) fuzzJump.getGameComponent().provideGameScreen();
        }
        return null;
    }

}
