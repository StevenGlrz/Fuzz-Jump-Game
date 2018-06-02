package com.fuzzjump.game.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.fuzzjump.api.model.user.ApiUser;
import com.fuzzjump.api.user.IUserService;
import com.fuzzjump.game.game.Assets;
import com.fuzzjump.game.game.player.Profile;
import com.fuzzjump.game.game.player.unlockable.UnlockableRepositoryService;
import com.fuzzjump.game.game.screen.ui.MainUI;
import com.fuzzjump.game.util.GraphicsScheduler;
import com.fuzzjump.libgdxscreens.screen.StageScreen;
import com.fuzzjump.libgdxscreens.screen.StageUI;
import com.google.gson.Gson;

import javax.inject.Inject;

public class MainScreen extends StageScreen<MainUI> {

    private final IUserService userService;
    private final Profile profile;
    private final UnlockableRepositoryService unlockables;
    private final Preferences preferences;
    private final GraphicsScheduler scheduler;
    private final Gson gson;

    @Inject
    public MainScreen(MainUI ui,
                      IUserService userService,
                      Profile profile,
                      UnlockableRepositoryService unlockables,
                      Preferences preferences,
                      GraphicsScheduler scheduler,
                      Gson gson) {
        super(ui);
        this.userService = userService;
        this.profile = profile;
        this.unlockables = unlockables;
        this.preferences = preferences;
        this.scheduler = scheduler;
        this.gson = gson;
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
            case Assets.MainUI.START_BUTTON:
                handleLogin();
                Gdx.input.setOnscreenKeyboardVisible(false);
                break;
        }
    }

    private void handleLogin() {
        StageUI ui = ui();
        Dialog waitingDialog = ui.actor(Dialog.class, Assets.MainUI.LOGIN_WAITING_MESSAGE_DIALOG);
        TextField userField = ui.actor(TextField.class, Assets.MainUI.LOGIN_USER_FIELD);

        if (userField.getText().isEmpty()) {
            userField.setMessageText("Please enter a username!");
            return;
        }
        ui.actor(Label.class, Assets.MainUI.LOGIN_DIALOG_MESSAGE).setText("Logging in...");
        ui.actor(Button.class, Assets.MainUI.LOGIN_DIALOG_OK).setVisible(false);
        waitingDialog.setName("Registering");
        showDialog(waitingDialog, getStage());

        userService.register(userField.getText()).observeOn(scheduler).switchMap(response -> {
            final ApiUser user = response.getBody();

            // Retrieve and remove since we don't want to persist username and password
            String username = user.getUsername();
            String password = user.getPassword();
            user.setUsername(null);
            user.setPassword(null);

            // Load and store profile data
            profile.loadUser(user);

            // Store profile data.
            // This doesn't persist unless our token retrieval was a success. We remove on error just in case.
            preferences.putString(Assets.PROFILE_DATA, gson.toJson(user));

            // UI process
            waitingDialog.setName("Loading game");
            screenHandler.showScreen(MenuScreen.class);

            return userService.retrieveToken(username, password);
        }).subscribe(tokenResponse -> {
            preferences.putString(Assets.USER_TOKEN, tokenResponse.getAccessToken());
            preferences.flush();
        }, e -> {
            preferences.remove(Assets.PROFILE_DATA);
            e.printStackTrace();
            // TODO Close on error
        });
    }

}
