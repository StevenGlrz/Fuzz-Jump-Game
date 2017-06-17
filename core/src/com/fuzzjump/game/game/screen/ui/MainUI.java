package com.fuzzjump.game.game.screen.ui;

import com.badlogic.gdx.Gdx;
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
        setupLoginTable();

        contentTable.add(switcher).fill().center().expand();
    }


    private void setupWelcomeTable() {
        Table welcomeTable = new Table();
        welcomeTable.setBackground(textures.getTextureRegionDrawable(Assets.UI_PANEL_WELCOME));
        Value padTop = Value.percentHeight(.025f, welcomeTable);
        Value padSides = Value.percentHeight(-.05f, welcomeTable);
        welcomeTable.defaults().padTop(padTop).padLeft(padSides).padRight(padSides).size(Value.percentWidth(.9f, welcomeTable), Value.percentWidth(0.2334149576573819f, welcomeTable));

        // TODO Locale's
        TextButton facebookButton = new TextButton("Login with Facebook", createFbTBStyle(this));
        facebookButton.getLabel().setAlignment(Align.center);
        facebookButton.getLabelCell().padRight(Value.percentWidth(.15f, facebookButton));

        TextButton kerpowButton = new TextButton("Register Name", createEmailTBStyle(this));
        kerpowButton.getLabel().setAlignment(Align.center);
        kerpowButton.getLabelCell().padRight(Value.percentWidth(.175f, kerpowButton));

        register(Assets.MainScreen.REGISTER_FACEBOOK, facebookButton);

        // Register click actions
        Helper.addClickAction(kerpowButton, (e, x, y) -> showRegistration());

        Value topBottomPadding = Value.percentHeight(.045f, welcomeTable);

        welcomeTable.add(facebookButton).padTop(topBottomPadding).row();
        welcomeTable.add(kerpowButton).row();


        switcher.addWidget(welcomeTable, Value.percentWidth(.975f, switcher), Value.percentWidth(0.64642315f, switcher));
    }

    private void showWelcomeTable() {
        switcher.setDisplayedChild(0);
        mainTable.setTitle("Welcome!");
    }

    private void showRegistration() {
        switcher.setDisplayedChild(1);
        mainTable.setTitle("Enter Username");
    }

    private void setupLoginTable() {
        register(Assets.MainScreen.LOGIN_USER_FIELD, new TextField("", editTextStyle));

        Label usernameLabel = new Label("Enter name:", getGameSkin());
        usernameLabel.setAlignment(Align.left);
        final TextField loginUserField = actor(TextField.class, Assets.MainScreen.LOGIN_USER_FIELD);

        Table loginTable = new Table();
        loginTable.setBackground(textures.getTextureRegionDrawable("ui-panel-login"));

        Value padTop = Value.percentHeight(.01f, loginTable);
        Value padBottom = Value.percentHeight(.05f, loginTable);
        Value padSides = Value.percentHeight(.0425f, loginTable);
        loginTable.defaults().padTop(padTop).padLeft(padSides).padRight(padSides).center().size(Value.percentWidth(.95f, loginTable), Value.percentWidth(0.132558402f, loginTable));

        loginTable.add(usernameLabel).colspan(2).left().row();
        loginTable.add(loginUserField).padTop(0).colspan(2).row();

        TextButton cancelBtn = new TextButton("Cancel", createXTBStyle(this));
        TextButton startBtn = new TextButton("Start", createPlayTBStyle(this));

        loginTable.add(new Actor()).colspan(2).center().expand().row();

        loginTable.add(cancelBtn).padBottom(padBottom).left().size(Value.percentWidth(.45f, loginTable), Value.percentWidth(0.15957446808510638297872340425532f, loginTable));
        loginTable.add(startBtn).padBottom(padBottom).right().size(Value.percentWidth(.45f, loginTable), Value.percentWidth(0.15957446808510638297872340425532f, loginTable));

        loginUserField.getStyle().background.setLeftWidth(Gdx.graphics.getWidth() / 40);
        loginUserField.getStyle().background.setRightWidth(Gdx.graphics.getWidth() / 35);

        Helper.addClickAction(cancelBtn, (e, x, y) -> showWelcomeTable());

        switcher.addWidget(loginTable, Value.percentWidth(.9f, switcher), Value.percentWidth(0.4836379999999998f, switcher));

        register(Assets.MainScreen.START_BUTTON, startBtn);
    }


    @Override
    public void backPressed() {

    }

}
