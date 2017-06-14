package com.fuzzjump.game.game.screens;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.fuzzjump.game.FuzzJump;
import com.fuzzjump.game.game.ScreenHandler;
import com.fuzzjump.game.game.StageIds;
import com.fuzzjump.game.game.StageScreen;
import com.fuzzjump.game.game.StageUI;
import com.fuzzjump.game.game.Textures;
import com.fuzzjump.game.game.screens.attachment.MenuScreenAttachment;
import com.fuzzjump.game.game.screens.attachment.ScreenAttachment;
import com.fuzzjump.game.game.ui.LoginUI;
import com.fuzzjump.game.net.requests.AuthenticationWebRequest;
import com.fuzzjump.game.net.requests.RegisterWebRequest;
import com.fuzzjump.game.net.requests.WebRequest;
import com.fuzzjump.game.net.requests.WebRequestCallback;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginScreen extends StageScreen {

    public LoginScreen(FuzzJump game, Textures textures, ScreenHandler handler) {
        super(game, textures, handler);
    }

    @Override
    public void load(ScreenAttachment attachment) {

    }

    @Override
    public void showScreen() {
        if (game.getProfile().getFacebookProfile().isValid()) {
            RegisterWebRequest webRequest = new RegisterWebRequest(game.getProfile().getFacebookProfile());
            webRequest.connect(registerCallback);
        }
    }


    private WebRequestCallback registerCallback = new WebRequestCallback() {
        @Override
        public void onResponse(JsonObject response) {
            try {
                register(response);
            } catch (Exception e) {
                // Growing up is realizing that I still need to do shit like this because of fucking Java
            }
        }
    };

    private void register(JsonObject response) {
        StageUI ui = ui();
        Dialog loginDialog = ui.actor(Dialog.class, StageIds.LoginScreen.LOGIN_WAITING_MESSAGE_DIALOG);
        TextField emailField = ui.actor(TextField.class, StageIds.LoginScreen.REGISTER_EMAIL_FIELD);

        if (response == null) {
            ui.actor(Label.class, StageIds.LoginScreen.LOGIN_DIALOG_MESSAGE).setText("Connection Error");
            ui.actor(Button.class, StageIds.LoginScreen.LOGIN_DIALOG_OK).setVisible(true);
            return;
        }
        if (response.has("Message")) {
            // Error occurred
            ui.actor(Label.class, StageIds.LoginScreen.LOGIN_DIALOG_MESSAGE).setText(response.get("Message").getAsString());
            ui.actor(Button.class, StageIds.LoginScreen.LOGIN_DIALOG_OK).setVisible(true);
            return;
        }
        switch (response.get(WebRequest.RESPONSE_KEY).getAsInt()) {
            case -1:
                emailField.setMessageText("E-mail is already in use");
                loginDialog.hide();
                break;
            case 1: // Success
                game.getProfile().load(response.get(WebRequest.PAYLOAD_KEY).getAsJsonObject());
                game.getProfile().save();
                screenHandler.showScreen(MenuScreen.class, new MenuScreenAttachment(false, false));
                break;
            default:
                ui.actor(Label.class, StageIds.LoginScreen.LOGIN_DIALOG_MESSAGE).setText("Unknown error");
                ui.actor(Button.class, StageIds.LoginScreen.LOGIN_DIALOG_OK).setVisible(true);
                break;
        }
    }

    private WebRequestCallback loginCallback = new WebRequestCallback() {
        @Override
        public void onResponse(JSONObject response) {
            login(response);
        }
    };

    private void login(JsonObject response) {
        if (response == null) {
            showError("Connection Error");
            return;
        }
        try {
            StageUI ui = ui();
            Dialog loginDialog = ui.actor(Dialog.class, StageIds.LoginScreen.LOGIN_WAITING_MESSAGE_DIALOG);
            TextField userField = ui.actor(TextField.class, StageIds.LoginScreen.LOGIN_USER_FIELD);
            TextField passwordField = ui.actor(TextField.class, StageIds.LoginScreen.LOGIN_PWD_FIELD);
            switch (response.get(WebRequest.RESPONSE_KEY).getAsInt()) {
                case -1:
                    showError("Invalid password");
                    break;
                case -2:
                    showError("Invalid email");
                    break;
                case 1:
                    game.getProfile().load(response.get(WebRequest.PAYLOAD_KEY).getAsJsonObject());
                    game.getProfile().save();
                    screenHandler.showScreen(MenuScreen.class, new MenuScreenAttachment(false, false));
                    break;
                default:
                    showError("Unknown response: " + response.get(WebRequest.RESPONSE_KEY).getAsInt());
                    break;
            }
        } catch (Exception e) {
            showError("Invalid response");
        }
    }

    private void showError(String error) {
        ui().actor(Label.class, StageIds.LoginScreen.LOGIN_DIALOG_MESSAGE).setText(error);
        ui().actor(Button.class, StageIds.LoginScreen.LOGIN_DIALOG_OK).setVisible(true);
    }

    @Override
    public void clicked(int id, Actor actor) {
        StageUI ui = ui();
        Dialog waitingDialog = ui.actor(Dialog.class, StageIds.LoginScreen.LOGIN_WAITING_MESSAGE_DIALOG);
        switch (id) {
            case StageIds.LoginScreen.LOGIN_BUTTON: {

                TextField userField = ui.actor(TextField.class, StageIds.LoginScreen.LOGIN_USER_FIELD);
                TextField passwordField = ui.actor(TextField.class, StageIds.LoginScreen.LOGIN_PWD_FIELD);

                if (userField.getText().isEmpty())
                    userField.setMessageText("Please enter your email");
                else if (passwordField.getText().isEmpty())
                    passwordField.setMessageText("Please enter a password");
                else {
                    ui.actor(Label.class, StageIds.LoginScreen.LOGIN_DIALOG_MESSAGE).setText("Attempting to login");
                    ui.actor(Button.class, StageIds.LoginScreen.LOGIN_DIALOG_OK).setVisible(false);
                    waitingDialog.setName("Logging in...");
                    waitingDialog.show(game.getStage());

                    new AuthenticationWebRequest(userField.getText(), passwordField.getText()).connect(loginCallback);
                }
                Gdx.input.setOnscreenKeyboardVisible(false);
            }
            break;
            case StageIds.LoginScreen.REGISTER_BUTTON: {

                TextField emailField = ui.actor(TextField.class, StageIds.LoginScreen.REGISTER_EMAIL_FIELD);
                TextField userField = ui.actor(TextField.class, StageIds.LoginScreen.REGISTER_USER_FIELD);
                TextField passwordField = ui.actor(TextField.class, StageIds.LoginScreen.REGISTER_PWD_FIELD);
                TextField reenterPasswordField = ui.actor(TextField.class, StageIds.LoginScreen.REGISTER_PWD_FIELD_2);

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
                    ui.actor(Label.class, StageIds.LoginScreen.LOGIN_DIALOG_MESSAGE).setText("Attempting to register");
                    ui.actor(Button.class, StageIds.LoginScreen.LOGIN_DIALOG_OK).setVisible(false);
                    waitingDialog.setName("Registering...");
                    waitingDialog.show(game.getStage());

                    new RegisterWebRequest(emailField.getText(), userField.getText(), passwordField.getText()).connect(registerCallback);
                }
                Gdx.input.setOnscreenKeyboardVisible(false);
            }
            break;
            case StageIds.LoginScreen.REGISTER_GMAIL:
                break;
            case StageIds.LoginScreen.REGISTER_FACEBOOK:
                game.loginWithFacebook();
                ui.actor(Label.class, StageIds.LoginScreen.LOGIN_DIALOG_MESSAGE).setText("Connecting to Facebook");
                ui.actor(Button.class, StageIds.LoginScreen.LOGIN_DIALOG_OK).setVisible(false);
                waitingDialog.setName("Registering...");
                waitingDialog.show(game.getStage());
                if (game.getProfile().getFacebookProfile().isValid()) {
                    RegisterWebRequest webRequest = new RegisterWebRequest(game.getProfile().getFacebookProfile());
                    webRequest.connect(registerCallback);
                }
                break;
        }
    }

    @Override
    public StageUI createUI() {
        return new LoginUI();
    }


}