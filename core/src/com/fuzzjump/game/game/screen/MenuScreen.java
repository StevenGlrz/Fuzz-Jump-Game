package com.fuzzjump.game.game.screen;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.fuzzjump.game.game.screen.ui.MenuUI;
import com.fuzzjump.libgdxscreens.StageScreen;

import javax.inject.Inject;

/**
 * Created by Steven Galarza on 6/16/2017.
 */
public class MenuScreen extends StageScreen<MenuUI> {

    @Inject
    public MenuScreen(MenuUI ui) {
        super(ui);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void showing() {

    }

    @Override
    public void clicked(int id, Actor actor) {

    }
}
