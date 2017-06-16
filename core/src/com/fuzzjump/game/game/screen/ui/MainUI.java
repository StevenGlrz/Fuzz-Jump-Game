package com.fuzzjump.game.game.screen.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.fuzzjump.game.game.Assets;
import com.fuzzjump.game.game.FuzzJumpGame;
import com.fuzzjump.game.game.screen.ui.components.ActorSwitcher;
import com.fuzzjump.game.game.screen.ui.components.FJDragDownBarTable;
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
        System.out.println(textures + " - " + skin);
    }

    @Override
    public void init() {
        this.editTextStyle = Assets.createETxtFieldStyle(this);

        setFillParent(true);

        Label messageLabel = new Label("Message", skin, "default");
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
        setupRegisterTable();

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
        facebookButton.getLabel().setAlignment(Align.right);
        facebookButton.getLabelCell().padRight(Value.percentWidth(.15f, facebookButton));
        TextButton gplusButton = new TextButton("Login with Google+", createGPlusTBStyle(this));
        gplusButton.getLabel().setAlignment(Align.right);
        gplusButton.getLabelCell().padRight(Value.percentWidth(.15f, gplusButton));
        TextButton kerpowButton = new TextButton("Login with email", createEmailTBStyle(this));
        kerpowButton.getLabel().setAlignment(Align.right);
        kerpowButton.getLabelCell().padRight(Value.percentWidth(.175f, kerpowButton));
        TextButton registerButton = new TextButton("Register account", createPlusTBStyle(this));
        registerButton.getLabel().setAlignment(Align.right);
        registerButton.getLabelCell().padRight(Value.percentWidth(.175f, registerButton));

        register(Assets.MainScreen.REGISTER_FACEBOOK, facebookButton);
        register(Assets.MainScreen.REGISTER_GMAIL, gplusButton);

        // Register click actions
        Helper.addClickAction(kerpowButton, (e, x, y) -> showLoginTable());
        Helper.addClickAction(registerButton, (e, x, y) -> showRegistertable());

        Value topBottomPadding = Value.percentHeight(.045f, welcomeTable);

        welcomeTable.add(facebookButton).padTop(topBottomPadding).row();
        welcomeTable.add(gplusButton).row();
        welcomeTable.add(kerpowButton).row();

        Label orLabel = new Label("or", getSkin(), "default");
        orLabel.setAlignment(Align.center);
        welcomeTable.add(orLabel).height(Value.percentHeight(.05f, welcomeTable)).expand().row();

        welcomeTable.add(registerButton).padBottom(topBottomPadding).row();

        switcher.addWidget(welcomeTable, Value.percentWidth(.975f, switcher), Value.percentWidth(1.14642315f, switcher));
    }

    private void setupRegisterTable() {
        register(Assets.MainScreen.REGISTER_EMAIL_FIELD, new TextField("", editTextStyle));
        register(Assets.MainScreen.REGISTER_USER_FIELD, new TextField("", editTextStyle));
        register(Assets.MainScreen.REGISTER_PWD_FIELD, new TextField("", editTextStyle));
        register(Assets.MainScreen.REGISTER_PWD_FIELD_2, new TextField("", editTextStyle));

        Label emailLabel = new Label("Email:", skin);
        final TextField registerEmailField = actor(TextField.class, Assets.MainScreen.REGISTER_EMAIL_FIELD);
        Label usernameLabel = new Label("Username:", skin);
        final TextField registerUserField = actor(TextField.class, Assets.MainScreen.REGISTER_USER_FIELD);
        Label passwordLabel = new Label("Password:", skin);
        final TextField passwordField = actor(TextField.class, Assets.MainScreen.REGISTER_PWD_FIELD);
        Label reEnterPassword = new Label("Reenter Password:", skin);
        final TextField reEnterPasswordField = actor(TextField.class, Assets.MainScreen.REGISTER_PWD_FIELD_2);

        passwordField.setPasswordMode(true);
        reEnterPasswordField.setPasswordMode(true);
        reEnterPasswordField.setPasswordCharacter('*');
        passwordField.setPasswordCharacter('*');

        Table registerTable = new Table();
        registerTable.setBackground(textures.getTextureRegionDrawable("ui-panel-welcome"));

        Value padTop = Value.percentHeight(.01f, registerTable);
        Value padBottom = Value.percentHeight(.025f, registerTable);
        Value padSides = Value.percentHeight(.025f, registerTable);
        registerTable.defaults().padTop(padTop).padLeft(padSides).padRight(padSides).center();

        registerTable.add(emailLabel).padTop(padBottom).left().colspan(2).row();
        registerTable.add(registerEmailField).colspan(2).size(Value.percentWidth(.95f, registerTable), Value.percentWidth(0.132558402f, registerTable)).row();

        registerTable.add(usernameLabel).left().colspan(2).row();
        registerTable.add(registerUserField).colspan(2).size(Value.percentWidth(.95f, registerTable), Value.percentWidth(0.132558402f, registerTable)).row();

        registerTable.add(passwordLabel).colspan(2).left().row();
        registerTable.add(passwordField).colspan(2).size(Value.percentWidth(.95f, registerTable), Value.percentWidth(0.132558402f, registerTable)).row();

        registerTable.add(reEnterPassword).colspan(2).left().row();
        registerTable.add(reEnterPasswordField).colspan(2).size(Value.percentWidth(.95f, registerTable), Value.percentWidth(0.132558402f, registerTable)).row();


        TextButton backBtn = new TextButton("Cancel", createXTBStyle(this));
        backBtn.getLabel().setAlignment(Align.right);
        backBtn.getLabelCell().padRight(Value.percentWidth(.15f, backBtn));
        TextButton registerBtn = new TextButton("Register", createPlayTBStyle(this));
        registerBtn.getLabel().setAlignment(Align.right);
        registerBtn.getLabelCell().padRight(Value.percentWidth(.075f, registerBtn));

        registerTable.add(new Actor()).colspan(2).center().expand().row();

        Value buttonPad = Value.percentWidth(.025f, registerTable);

        padBottom = Value.percentHeight(.04f, registerTable);

        registerTable.add(backBtn).padBottom(padBottom).padLeft(buttonPad).left().size(Value.percentWidth(.45f, registerTable), Value.percentWidth(0.15957446808510638297872340425532f, registerTable));
        registerTable.add(registerBtn).padBottom(padBottom).padRight(buttonPad).right().size(Value.percentWidth(.45f, registerTable), Value.percentWidth(0.15957446808510638297872340425532f, registerTable));

        //Label label = new Label("Kerpow Games", skin, "small");
        //label.setAlignment(Align.bottom, Align.center);
        //registerTable.add(label).colspan(2).center().padBottom(10f).expand();


        Helper.addClickAction(backBtn, (e, x, y) -> {
            if (e.getType() == InputEvent.Type.touchUp) {
                showWelcomeTable();
                Gdx.input.setOnscreenKeyboardVisible(false);
            }
        });

        switcher.addWidget(registerTable, Value.percentWidth(.975f, switcher), Value.percentWidth(1.14642315f, switcher));

        register(Assets.MainScreen.REGISTER_BUTTON, registerBtn);
    }

    private void showWelcomeTable() {
        switcher.setDisplayedChild(0);
        mainTable.setTitle("Welcome!");
    }

    private void showLoginTable() {
        switcher.setDisplayedChild(1);
        mainTable.setTitle("Login");
    }

    private void showRegistertable() {
        switcher.setDisplayedChild(2);
        mainTable.setTitle("Register");
    }

    private void setupLoginTable() {
        register(Assets.MainScreen.LOGIN_USER_FIELD, new TextField("", editTextStyle));
        register(Assets.MainScreen.LOGIN_PWD_FIELD, new TextField("", editTextStyle));

        Label usernameLabel = new Label("Email:", skin);
        usernameLabel.setAlignment(Align.left);
        final TextField loginUserField = actor(TextField.class, Assets.MainScreen.LOGIN_USER_FIELD);
        Label passwordLabel = new Label("Password:", skin);
        passwordLabel.setAlignment(Align.left);
        final TextField loginPassField = actor(TextField.class, Assets.MainScreen.LOGIN_PWD_FIELD);
        loginPassField.setPasswordMode(true);
        loginPassField.setPasswordCharacter('*');

        Table loginTable = new Table();
        loginTable.setBackground(textures.getTextureRegionDrawable("ui-panel-login"));

        Value padTop = Value.percentHeight(.01f, loginTable);
        Value padBottom = Value.percentHeight(.05f, loginTable);
        Value padSides = Value.percentHeight(.0425f, loginTable);
        loginTable.defaults().padTop(padTop).padLeft(padSides).padRight(padSides).center().size(Value.percentWidth(.95f, loginTable), Value.percentWidth(0.132558402f, loginTable));

        loginTable.add(usernameLabel).colspan(2).left().row();
        loginTable.add(loginUserField).padTop(0).colspan(2).row();
        loginTable.add(passwordLabel).colspan(2).left().row();
        loginTable.add(loginPassField).padTop(0).colspan(2).row();

        TextButton cancelBtn = new TextButton("Cancel", createXTBStyle(this));
        TextButton loginBtn = new TextButton("Login", createPlayTBStyle(this));

        loginTable.add(new Actor()).colspan(2).center().expand().row();

        loginTable.add(cancelBtn).padBottom(padBottom).left().size(Value.percentWidth(.45f, loginTable), Value.percentWidth(0.15957446808510638297872340425532f, loginTable));
        loginTable.add(loginBtn).padBottom(padBottom).right().size(Value.percentWidth(.45f, loginTable), Value.percentWidth(0.15957446808510638297872340425532f, loginTable));

        loginUserField.getStyle().background.setLeftWidth(Gdx.graphics.getWidth() / 40);
        loginUserField.getStyle().background.setRightWidth(Gdx.graphics.getWidth() / 35);

        Helper.addClickAction(cancelBtn, (e, x, y) -> showWelcomeTable());

        switcher.addWidget(loginTable, Value.percentWidth(.9f, switcher), Value.percentWidth(0.7036379999999998f, switcher));

        register(Assets.MainScreen.LOGIN_BUTTON, loginBtn);
    }


    @Override
    public void backPressed() {

    }

}
