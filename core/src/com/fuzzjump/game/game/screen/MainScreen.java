package com.fuzzjump.game.game.screen;

import com.fuzzjump.game.game.screen.ui.MainUI;
import com.fuzzjump.game.service.user.IUserService;
import com.fuzzjump.libgdxscreens.StageScreen;

import javax.inject.Inject;

public class MainScreen extends StageScreen<MainUI> {

    private final IUserService userService;

    //We can get rid of the UI from the constructor, I dont feel like doing that rn tho
    @Inject
    public MainScreen(MainUI ui, IUserService userService) {
        super(ui);
        this.userService = userService;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void onPreRender(float delta) {

    }

    @Override
    public void onPostRender(float delta) {

    }

    @Override
    public void showing() {

    }

}
