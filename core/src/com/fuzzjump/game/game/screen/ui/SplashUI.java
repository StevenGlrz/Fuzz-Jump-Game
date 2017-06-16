package com.fuzzjump.game.game.screen.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.fuzzjump.game.game.FuzzJumpGame;
import com.fuzzjump.libgdxscreens.StageUI;
import com.fuzzjump.libgdxscreens.Textures;

import javax.inject.Inject;

public class SplashUI extends StageUI {

    @Inject
    public SplashUI(Textures textures) {
        super(textures);
    }

    @Override
    public void init() {
    }

    @Override
    public void backPressed() {

    }

}
