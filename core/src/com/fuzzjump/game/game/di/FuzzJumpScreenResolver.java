package com.fuzzjump.game.game.di;

import com.fuzzjump.game.FuzzJump;
import com.fuzzjump.game.game.screen.MainScreen;
import com.fuzzjump.libgdxscreens.ScreenResolver;
import com.fuzzjump.libgdxscreens.StageScreen;

public class FuzzJumpScreenResolver implements ScreenResolver {

    private final FuzzJump fuzzJump;

    public FuzzJumpScreenResolver(FuzzJump fuzzJump) {
        this.fuzzJump = fuzzJump;
    }

    @Override
    public <T extends StageScreen> T resolveScreen(Class<T> screenClazz) {
        if (screenClazz == MainScreen.class) {
            return (T) fuzzJump.getGameComponent().provideMainScreen();
        }
        return null;
    }

}
