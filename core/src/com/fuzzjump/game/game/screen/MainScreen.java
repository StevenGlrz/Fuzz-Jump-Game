package com.fuzzjump.game.game.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.fuzzjump.game.game.FuzzJumpGame;
import com.fuzzjump.game.game.screen.ui.MainUI;
import com.fuzzjump.game.service.user.IUserService;
import com.fuzzjump.libgdxscreens.ScreenHandler;
import com.fuzzjump.libgdxscreens.StageScreen;
import com.fuzzjump.libgdxscreens.Textures;

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
    public void render(float delta) {
    }

    @Override
    public void showing() {

    }

}
