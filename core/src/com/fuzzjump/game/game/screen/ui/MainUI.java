package com.fuzzjump.game.game.screen.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Align;
import com.fuzzjump.game.game.Assets;
import com.fuzzjump.game.game.screen.component.ActorSwitcher;
import com.fuzzjump.game.game.screen.component.FJDragDownBarTable;
import com.fuzzjump.game.util.Helper;
import com.fuzzjump.libgdxscreens.StageUI;
import com.fuzzjump.libgdxscreens.Textures;

import javax.inject.Inject;

import static com.fuzzjump.game.game.Assets.*;

public class MainUI extends StageUI {

    public static final float LOGIN_TABLE_HEIGHT = .6f;

    private FJDragDownBarTable mainTable;

    private ActorSwitcher switcher;

    private TextField.TextFieldStyle editTextStyle;

    @Inject
    public MainUI(Textures textures, Skin skin) {
        super(textures, skin);
    }

    @Override
    public void init() {
        this.editTextStyle = Assets.createETxtFieldStyle(this);
        this.editTextStyle.messageFontColor = Color.WHITE;

        setFillParent(true);

        Label messageLabel = new Label("Message", getGameSkin(), "default");
        Image progressImage = new Image(textures.getTextureRegionDrawable(Assets.UI_PROGRESS_SPINNER));
        progressImage.setOrigin(Align.center);
        progressImage.addAction(Actions.forever(Actions.rotateBy(5f, .01f)));

        Dialog loginDialog = new Dialog("", createDialogStyle(this)) {
            @Override
            public float getPrefWidth() {
                return Gdx.graphics.getWidth() * 0.65f;
            }

            @Override
            public float getPrefHeight() {
                return Gdx.graphics.getWidth() * 0.5081829277777778f;
            }
        };

        final TextButton okButton = new TextButton("OK", Assets.createDefaultTBStyle(this));

        loginDialog.setObject(okButton, okButton);
        loginDialog.setModal(true);

        register(Assets.MainScreen.LOGIN_WAITING_MESSAGE_DIALOG, loginDialog);
        register(Assets.MainScreen.LOGIN_DIALOG_MESSAGE, messageLabel);
        register(Assets.MainScreen.LOGIN_DIALOG_OK, okButton);
        register(Assets.MainScreen.LOGIN_WAITING_MESSAGE_DIALOG, loginDialog);

        loginDialog.getContentTable().add(messageLabel).padTop(Value.percentHeight(.1f, loginDialog)).row();
        loginDialog.getContentTable().add(progressImage).center().expand().size(Value.percentWidth(.25f, loginDialog));
        loginDialog.getButtonTable().add(okButton).size(Value.percentWidth(.5f, loginDialog), Value.percentWidth(0.1296749444444444f, loginDialog));

        switcher = new ActorSwitcher();
        mainTable = new FJDragDownBarTable(this);

        add(mainTable).expand().fill();
        Table contentTable = mainTable.getContentTable();

        setupWelcomeTable();

        contentTable.add(switcher).fill().center().expand();
    }


    private void setupWelcomeTable() {
        Table welcomeTable = new Table();
        welcomeTable.setBackground(textures.getTextureRegionDrawable(Assets.UI_PANEL_WELCOME));
        Value padBottom = Value.percentHeight(.05f, welcomeTable);
        // TODO Locale's

        Value topBottomPadding = Value.percentHeight(.045f, welcomeTable);

        register(Assets.MainScreen.LOGIN_USER_FIELD, new TextField("", editTextStyle));

        final Label messageLabel = new Label("To begin, enter a display name\n or use your facebook account.", getGameSkin(), "profile");
        welcomeTable.add(messageLabel).align(Align.center).center().expand().colspan(2).row();

        final TextField loginUserField = actor(TextField.class, Assets.MainScreen.LOGIN_USER_FIELD);
        loginUserField.setMessageText("Enter display name");
        welcomeTable.add(loginUserField).padTop(0).colspan(2).size(Value.percentWidth(.95f, welcomeTable), Value.percentWidth(0.132558402f, welcomeTable)).row();

        loginUserField.getStyle().background.setLeftWidth(Gdx.graphics.getWidth() / 40);
        loginUserField.getStyle().background.setRightWidth(Gdx.graphics.getWidth() / 35);

        TextButton facebookButton = new TextButton("Facebook", createFbTBStyle(this));
        facebookButton.getLabel().setAlignment(Align.right);
        facebookButton.getLabelCell().padRight(Value.percentWidth(.075f, facebookButton));

        TextButton startBtn = new TextButton("Start", createPlayTBStyle(this));
        facebookButton.getLabel().setAlignment(Align.right);
        facebookButton.getLabelCell().padRight(Value.percentWidth(.075f, facebookButton));

        welcomeTable.add(facebookButton).padLeft(Value.percentWidth(.025f, welcomeTable)).padTop(topBottomPadding).padBottom(topBottomPadding).left().size(Value.percentWidth(.45f, welcomeTable), Value.percentWidth(0.15957446808510638297872340425532f, welcomeTable));
        welcomeTable.add(startBtn).padRight(Value.percentWidth(.025f, welcomeTable)).padTop(topBottomPadding).padBottom(topBottomPadding).right().size(Value.percentWidth(.45f, welcomeTable), Value.percentWidth(0.15957446808510638297872340425532f, welcomeTable));

        register(Assets.MainScreen.START_BUTTON, startBtn);
        register(Assets.MainScreen.REGISTER_FACEBOOK, facebookButton);

        switcher.addWidget(welcomeTable, Value.percentWidth(.8f, switcher), Value.percentWidth(0.5303984820512819f, switcher));
    }

    private void showRegistration() {
        switcher.setDisplayedChild(1);
    }


    @Override
    public void backPressed() {

    }

}
