package com.fuzzjump.game.game.screen;

import com.fuzzjump.game.game.screen.ui.SplashUI;
import com.fuzzjump.game.service.user.IUserService;
import com.fuzzjump.libgdxscreens.StageScreen;

import javax.inject.Inject;

public class SplashScreen extends StageScreen<SplashUI> {

    private final IUserService userService;

    //We can get rid of the UI from the constructor, I dont feel like doing that rn tho
    @Inject
    public SplashScreen(SplashUI ui, IUserService userService) {
        super(ui);
        this.userService = userService;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void showing() {

    }

}
