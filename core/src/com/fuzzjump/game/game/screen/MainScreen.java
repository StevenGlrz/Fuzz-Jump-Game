package com.fuzzjump.game.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.fuzzjump.game.game.Assets;
import com.fuzzjump.game.game.player.Profile;
import com.fuzzjump.game.game.player.unlockable.UnlockableRepository;
import com.fuzzjump.game.game.screen.ui.MainUI;
import com.fuzzjump.game.service.user.IUserService;
import com.fuzzjump.libgdxscreens.screen.StageScreen;
import com.fuzzjump.libgdxscreens.screen.StageUI;

import javax.inject.Inject;

public class MainScreen extends StageScreen<MainUI> {

    private final IUserService userService;

    //We can get rid of the UI from the constructor, I dont feel like doing that rn tho
    @Inject
    public MainScreen(MainUI ui, IUserService userService, Profile profile, UnlockableRepository definitions) {
        super(ui);
        this.userService = userService;

        profile.getAppearance().createDummy(definitions); // TODO THIS IS TEMPORARY
    }

    @Override
    public void onReady() {

    }

    @Override
    public void onShow() {

    }

    @Override
    public void clicked(int id, Actor actor) {
        StageUI ui = getUI();
        Dialog waitingDialog = ui.actor(Dialog.class, Assets.MainScreen.LOGIN_WAITING_MESSAGE_DIALOG);
        switch (id) {
            case Assets.MainScreen.START_BUTTON: {

                TextField userField = ui.actor(TextField.class, Assets.MainScreen.LOGIN_USER_FIELD);

                if (userField.getText().isEmpty()) {
                    userField.setMessageText("Please enter your email");
                } else {
                    ui.actor(Label.class, Assets.MainScreen.LOGIN_DIALOG_MESSAGE).setText("Logging in...");
                    ui.actor(Button.class, Assets.MainScreen.LOGIN_DIALOG_OK).setVisible(false);
                    waitingDialog.setName("Logging in...");
                    showDialog(waitingDialog, getStage());

                    screenHandler.showScreen(MenuScreen.class);
                }
                Gdx.input.setOnscreenKeyboardVisible(false);
            }
            break;
            case Assets.MainScreen.REGISTER_BUTTON: {

                TextField emailField = ui.actor(TextField.class, Assets.MainScreen.REGISTER_EMAIL_FIELD);
                TextField userField = ui.actor(TextField.class, Assets.MainScreen.REGISTER_USER_FIELD);
                TextField passwordField = ui.actor(TextField.class, Assets.MainScreen.REGISTER_PWD_FIELD);
                TextField reenterPasswordField = ui.actor(TextField.class, Assets.MainScreen.REGISTER_PWD_FIELD_2);

                if (emailField.getText().isEmpty())
                    emailField.setMessageText("Please enter an email");
                else if (userField.getText().isEmpty())
                    userField.setMessageText("Please enter a username");
                else if (passwordField.getText().isEmpty())
                    passwordField.setMessageText("Please enter a password");
                else if (reenterPasswordField.getText().isEmpty())
                    reenterPasswordField.setMessageText("Please enter a password");
                else if (!passwordField.getText().equals(reenterPasswordField.getText()))
                    reenterPasswordField.setMessageText("Must match password");
                else {
                    ui.actor(Label.class, Assets.MainScreen.LOGIN_DIALOG_MESSAGE).setText("Attempting to register");
                    ui.actor(Button.class, Assets.MainScreen.LOGIN_DIALOG_OK).setVisible(false);
                    waitingDialog.setName("Registering...");
                    waitingDialog.show(getStage());

                }
                Gdx.input.setOnscreenKeyboardVisible(false);
            }
            break;
            case Assets.MainScreen.REGISTER_FACEBOOK:

                break;
        }
    }

}
