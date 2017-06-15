package com.fuzzjump.game.game.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.fuzzjump.game.game.FuzzJumpGame;
import com.fuzzjump.game.game.screen.ui.MainUI;
import com.fuzzjump.libgdxscreens.ScreenHandler;
import com.fuzzjump.libgdxscreens.StageScreen;
import com.fuzzjump.libgdxscreens.StageUI;
import com.fuzzjump.libgdxscreens.Textures;

import javax.inject.Inject;

public class MainScreen extends StageScreen<MainUI> {

    @Inject
    public MainScreen(Stage stage, Textures textures, ScreenHandler handler, MainUI ui) {
        super(stage, textures, handler, ui);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void showing() {

    }

}
