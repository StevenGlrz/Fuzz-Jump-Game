package com.fuzzjump.game.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.fuzzjump.api.user.IUserService;
import com.fuzzjump.game.game.Assets;
import com.fuzzjump.game.game.player.Profile;
import com.fuzzjump.game.game.player.unlockable.UnlockableRepository;
import com.fuzzjump.game.game.screen.ui.MainUI;
import com.fuzzjump.game.util.GraphicsScheduler;
import com.fuzzjump.libgdxscreens.screen.StageScreen;
import com.fuzzjump.libgdxscreens.screen.StageUI;
import com.google.gson.JsonObject;

import javax.inject.Inject;

public class MainScreen extends StageScreen<MainUI> {

    private final IUserService userService;
    private final Profile profile;
    private final UnlockableRepository unlockables;
    private final Preferences preferences;
    private final GraphicsScheduler scheduler;

    @Inject
    public MainScreen(MainUI ui, IUserService userService, Profile profile, UnlockableRepository unlockables, Preferences preferences, GraphicsScheduler scheduler) {
        super(ui);
        this.userService = userService;
        this.profile = profile;
        this.unlockables = unlockables;
        this.preferences = preferences;
        this.scheduler = scheduler;
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
            case Assets.MainScreen.START_BUTTON:
                handleLogin();
                Gdx.input.setOnscreenKeyboardVisible(false);
                break;
        }
    }

    private void handleLogin() {
        StageUI ui = ui();
        Dialog waitingDialog = ui.actor(Dialog.class, Assets.MainScreen.LOGIN_WAITING_MESSAGE_DIALOG);
        TextField userField = ui.actor(TextField.class, Assets.MainScreen.LOGIN_USER_FIELD);

        if (userField.getText().isEmpty()) {
            userField.setMessageText("Please enter a username!");
        } else {
            ui.actor(Label.class, Assets.MainScreen.LOGIN_DIALOG_MESSAGE).setText("Logging in...");
            ui.actor(Button.class, Assets.MainScreen.LOGIN_DIALOG_OK).setVisible(false);
            waitingDialog.setName("Registering");
            showDialog(waitingDialog, getStage());

            userService.login(userField.getText()).observeOn(scheduler).subscribe(r -> {
                if (r != null && r.isSuccess()) {
                    JsonObject data = r.getBody();

                    // Retrieve password and remove it from the JSON object so it doesn't persist.
                    String password = data.get("password").getAsString();
                    data.remove("password");

                    // Load and store profile data
                    profile.load(data);
                    preferences.putString(Assets.PROFILE_DATA, data.toString());

                    // Acquire token from API and persist preferences
                    userService.retrieveToken(profile.getApiName(), password).subscribe();

                    // UI process
                    waitingDialog.setName("Loading game");
                    screenHandler.showScreen(MenuScreen.class);
                } else {
                    // TODO Error handling
                }
            }, e -> {
                e.printStackTrace();
                // TODO Failed to register
            });
        }
    }

}
