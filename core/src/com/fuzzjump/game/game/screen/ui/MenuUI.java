package com.fuzzjump.game.game.screen.ui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Align;
import com.fuzzjump.game.game.Assets;
import com.fuzzjump.game.game.player.unlockable.UnlockableColorizer;
import com.fuzzjump.game.game.screen.component.ActorSwitcher;
import com.fuzzjump.game.game.screen.component.FJDragDownBarTable;
import com.fuzzjump.game.game.screen.component.Fuzzle;
import com.fuzzjump.game.game.player.Profile;
import com.fuzzjump.game.game.player.unlockable.UnlockableRepository;
import com.fuzzjump.game.util.Helper;
import com.fuzzjump.libgdxscreens.StageUI;
import com.fuzzjump.libgdxscreens.Textures;

import javax.inject.Inject;

import static com.fuzzjump.game.game.Assets.createDefaultTBStyle;
import static com.fuzzjump.game.game.Assets.createDialogStyle;
import static com.fuzzjump.game.game.Assets.createLeaderboardBtnStyle;
import static com.fuzzjump.game.game.Assets.createPlayTBStyle;
import static com.fuzzjump.game.game.Assets.createPlusTBStyle;
import static com.fuzzjump.game.game.Assets.createSettingsBtnStyle;
import static com.fuzzjump.game.game.Assets.createStoreBtnStyle;

/**
 * Created by Steven Galarza on 6/16/2017.
 */
public class MenuUI extends StageUI {

    private ActorSwitcher uiSwitcher;
    private CharacterSelectionUI profileUI;
    private SettingsUI settingsUI;

    private FJDragDownBarTable dropdownTable;

    private final Profile profile;
    private final UnlockableRepository definitions;
    private final UnlockableColorizer colorizer;

    private final Stage stage;

    @Inject
    public MenuUI(Textures textures, Skin skin, Stage stage, Profile profile, UnlockableRepository definitions, UnlockableColorizer colorizer) {
        super(textures, skin);
        this.profile = profile;
        this.stage = stage;
        this.definitions = definitions;
        this.colorizer = colorizer;

        profile.getAppearance().createDummy(definitions);
    }

    @Override
    public void init() {
        dropdownTable = new FJDragDownBarTable(this); //, getGame().getProfile());
        uiSwitcher = new ActorSwitcher();

        Label messageLabel = new Label("Loading", getSkin(), "default");
        final Image progressImage = new Image(textures.getTextureRegionDrawable(Assets.UI_PROGRESS_SPINNER));
        final TextButton progressCloseButton = new TextButton("Close", createDefaultTBStyle(this));
        progressImage.setOrigin(Align.center);
        progressImage.addAction(Actions.forever(Actions.rotateBy(5f, .01f)));

        final Dialog progressDialog = new Dialog("", createDialogStyle(this)) {

            @Override
            public float getPrefWidth() {
                return Gdx.graphics.getWidth() * 0.65f;
            }

            @Override
            public float getPrefHeight() {
                return Gdx.graphics.getWidth() * 0.5081829277777778f;
            }


        };
        progressDialog.setModal(true);
        progressDialog.getContentTable().add(messageLabel).padTop(Value.percentHeight(.1f, progressDialog)).row();
        progressDialog.getContentTable().add(progressImage).center().expand().size(Value.percentWidth(.25f, progressDialog));
        progressCloseButton.setVisible(false);

        progressDialog.getButtonTable().add(progressCloseButton).size(Value.percentWidth(.475f, progressDialog), Value.percentWidth(0.1315789473684211f, progressDialog)).padBottom(Value.percentHeight(.035f, progressDialog)).center().expand();

        register(Assets.MenuUI.PROGRESS_DIALOG, progressDialog);
        register(Assets.MenuUI.PROGRESS_LABEL, messageLabel);
        register(Assets.MenuUI.CLOSE_BUTTON, progressCloseButton);
        register(Assets.MenuUI.PROGRESS_IMAGE, progressImage);

        this.profileUI = new CharacterSelectionUI(this, stage, profile, definitions, colorizer);
        this.settingsUI = new SettingsUI(this);

        setFillParent(true);

        Table menuTable = new Table();
        menuTable.setBackground(textures.getTextureRegionDrawable("ui-panel-mainmenu"));

        Value padSides = Value.percentWidth(.05f, menuTable);
        Value padTopBottom = Value.percentHeight(.05f, menuTable);

        Table innerTable = new Table();
        menuTable.add(innerTable).pad(padTopBottom, padSides, padTopBottom, padSides).size(Value.percentWidth(.95f, menuTable), Value.percentHeight(.9f, menuTable)).expand();

        Value topRowHeight = Value.percentHeight(.65f, innerTable);

        Table buttonsTable = new Table();
        TextButton publicGameButton = new TextButton("Find Game", createPlayTBStyle(this));
        publicGameButton.getLabel().setAlignment(Align.right);
        publicGameButton.getLabelCell().padRight(Value.percentWidth(.15f, publicGameButton));
        TextButton privateGameButton = new TextButton("New Game", createPlusTBStyle(this));
        privateGameButton.getLabel().setAlignment(Align.right);
        privateGameButton.getLabelCell().padRight(Value.percentWidth(.05f, privateGameButton));
        TextButton friendsButton = new TextButton("Friends", createDefaultTBStyle(this));
        buttonsTable.defaults().size(Value.percentWidth(.95f, buttonsTable), Value.percentWidth(0.25f, buttonsTable));
        buttonsTable.add(publicGameButton).expand().row();
        buttonsTable.add(privateGameButton).expand().row();
        buttonsTable.add(friendsButton).expand().row();
        innerTable.add(buttonsTable).left().padRight(padSides).expand().size(Value.percentWidth(.55f, innerTable), topRowHeight);


        Table pictureTable = new Table();
        Fuzzle fuzzle = new Fuzzle(this, definitions, colorizer);
        TextButton profileButton = new TextButton("Customize", createDefaultTBStyle(this));
        pictureTable.add(fuzzle).size(Value.percentWidth(0.75f, pictureTable)).expand().row();
        pictureTable.add(profileButton).padBottom(Value.percentHeight(.0416f, pictureTable)).size(Value.percentWidth(.95f, pictureTable), Value.percentWidth(0.25f, pictureTable));
        innerTable.add(pictureTable).right().expand().size(Value.percentWidth(.4f, innerTable), topRowHeight);

        innerTable.row();

        Table lowerButtonsTable = new Table();
        Table btnGroup = new Table();

        ImageButton leaderboardBtn = new ImageButton(createLeaderboardBtnStyle(this));
        ImageButton storeBtn = new ImageButton(createStoreBtnStyle(this));
        ImageButton settingsBtn = new ImageButton(createSettingsBtnStyle(this));

        Value btnHeight = Value.percentHeight(1f, btnGroup);
        Value btnWidth = btnHeight;
        Value btnPad = Value.percentWidth(.025f, btnGroup);
        btnGroup.defaults().size(btnWidth, btnHeight);
        btnGroup.add(leaderboardBtn).expand();
        btnGroup.add(storeBtn).expand().padLeft(btnPad).padRight(btnPad);
        btnGroup.add(settingsBtn).expand();

        lowerButtonsTable.add(btnGroup).size(Value.percentWidth(.55f, lowerButtonsTable), Value.percentHeight(.75f, lowerButtonsTable));

        innerTable.add(lowerButtonsTable).colspan(2).size(Value.percentWidth(1f, innerTable), Value.percentHeight(.3f, innerTable)).center().expand();

        register(Assets.MenuUI.PUBLIC_GAME, publicGameButton);


        uiSwitcher.addWidget(menuTable, Value.percentWidth(.975f, uiSwitcher), Value.percentWidth(0.8061598235294119f, uiSwitcher));
        //uiSwitcher.addWidget(friendsUI, Value.percentWidth(.9f, uiSwitcher), Value.percentWidth(0.8288981077080586f, uiSwitcher));
        uiSwitcher.addWidget(profileUI, Value.percentWidth(.9f, uiSwitcher), Value.percentHeight(.5f, uiSwitcher));
        uiSwitcher.addWidget(settingsUI);


        add(dropdownTable).expand().fill();

        Table contentTable = dropdownTable.getContentTable();

        contentTable.add(uiSwitcher).fill().center().expand();

        // Register click listener
        Helper.addClickAction(profileButton, (e, x, y) -> {
            profileUI.showing();
            uiSwitcher.setDisplayedChild(1);
        });
        Helper.addClickAction(progressCloseButton, (e, x, y) -> {
            progressDialog.hide();
            progressCloseButton.setVisible(false);
        });
        //Helper.addClickAction(friendsButton, (e, x, y) -> uiSwitcher.setDisplayedChild(1));
        Helper.addClickAction(settingsBtn, (e, x, y) -> uiSwitcher.setDisplayedChild(2));
        Gdx.app.postRunnable(() -> {
            profileUI.init();
            settingsUI.init();
        });
    }

    @Override
    public void backPressed() {

    }

    public void showMain() {
        uiSwitcher.setDisplayedChild(0);
    }
}