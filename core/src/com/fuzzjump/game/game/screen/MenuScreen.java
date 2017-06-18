package com.fuzzjump.game.game.screen;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.fuzzjump.game.game.screen.ui.MenuUI;
import com.fuzzjump.libgdxscreens.StageScreen;

import javax.inject.Inject;

import static com.fuzzjump.game.game.Assets.MenuUI.PUBLIC_GAME;

/**
 * Created by Steven Galarza on 6/16/2017.
 */
public class MenuScreen extends StageScreen<MenuUI> {

    @Inject
    public MenuScreen(MenuUI ui) {
        super(ui);
    }

    @Override
    public void onReady() {

    }

    @Override
    public void showing() {

    }

    @Override
    public void clicked(int id, Actor actor) {
        switch (id) {
            case PUBLIC_GAME:
                screenHandler.showScreen(WaitingScreen.class);
                break;
        }
    }
}
