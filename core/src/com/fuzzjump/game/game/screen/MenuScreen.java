package com.fuzzjump.game.game.screen;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.fuzzjump.game.game.Assets;
import com.fuzzjump.game.game.player.Profile;
import com.fuzzjump.game.game.screen.ui.CharacterSelectionUI;
import com.fuzzjump.game.game.screen.ui.MenuUI;
import com.fuzzjump.libgdxscreens.screen.StageScreen;

import javax.inject.Inject;

import static com.fuzzjump.game.game.Assets.MenuUI.PUBLIC_GAME;
import static com.fuzzjump.game.game.Assets.MenuUI.SELECT_BUY_BUTTON;

/**
 * Created by Steven Galarza on 6/16/2017.
 */
public class MenuScreen extends StageScreen<MenuUI> {

    private final Profile profile;

    @Inject
    public MenuScreen(MenuUI ui, Profile profile) {
        super(ui);
        this.profile = profile;
    }

    @Override
    public void onReady() {

    }

    @Override
    public void onShow() {

    }

    @Override
    public void clicked(int id, Actor actor) {
        switch (id) {
            case PUBLIC_GAME:
                screenHandler.showScreen(WaitingScreen.class);
                break;
            case SELECT_BUY_BUTTON:
                buySelectedUnlockable();
                break;
        }
    }

    private void buySelectedUnlockable() {
        final Dialog progressDialog = ui().actor(Dialog.class, Assets.MenuUI.PROGRESS_DIALOG);
        final Image image = ui().actor(Assets.MenuUI.PROGRESS_IMAGE);
        final Label status = ui().actor(Assets.MenuUI.PROGRESS_LABEL);
        final Button closeButton = ui().actor(Assets.MenuUI.CLOSE_BUTTON);
        final CharacterSelectionUI.UnlockableEntry buyEntry = ui().actor(Assets.MenuUI.SELECTED_UNLOCK);

        if (buyEntry.getUnlockableDefinition().getCost() > profile.getCoins()) {
            image.setVisible(false);
            status.setVisible(true);
            closeButton.setVisible(true);
            status.setText("Not enough coins!");
            progressDialog.show(getStage());
            return;
        }
        //ui().actor(Dialog.class, Assets.MenuUI.BUYING_DIALOG).hide();

        image.setVisible(true);
        status.setVisible(true);
        closeButton.setVisible(false);
        progressDialog.show(getStage());
        status.setText("Purchasing...");
        ;
    }
}
