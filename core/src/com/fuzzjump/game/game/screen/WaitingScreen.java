package com.fuzzjump.game.game.screen;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.fuzzjump.game.game.Assets;
import com.fuzzjump.game.game.screen.ui.WaitingUI;
import com.fuzzjump.libgdxscreens.StageScreen;

import javax.inject.Inject;

public class WaitingScreen extends StageScreen<WaitingUI> {

    public static final int MAXIMUM_PLAYERS = 4;

    private Dialog progressDialog;
    private Image image;
    private Label status;
    private Button closeButton;

    @Inject
    public WaitingScreen(WaitingUI ui) {
        super(ui);
    }

    @Override
    public void initialize() {
/*        this.progressDialog = getUI().actor(Dialog.class, Assets.WaitingUI.PROGRESS_DIALOG);
        this.image = getUI().actor(Image.class, Assets.WaitingUI.PROGRESS_IMAGE);
        this.status = getUI().actor(Label.class, Assets.WaitingUI.PROGRESS_LABEL);
        this.closeButton = getUI().actor(Button.class, Assets.WaitingUI.CLOSE_BUTTON);
        image.setVisible(true);
        status.setVisible(true);
        closeButton.setVisible(false);
        status.setText("Finding game...");
        progressDialog.show(stage);*/
    }

    @Override
    public void showing() {

    }

    @Override
    public void clicked(int id, Actor actor) {
        switch (id) {
            case Assets.WaitingUI.READY_BUTTON:
                break;
            case Assets.WaitingUI.CANCEL_BUTTON:
                screenHandler.showScreen(MenuScreen.class);
                break;
            case Assets.WaitingUI.MAP_BUTTON:
                break;
        }
    }
}
