package com.fuzzjump.game.game.screen.ui;

import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.fuzzjump.game.game.Assets;
import com.fuzzjump.game.util.Helper;
import com.fuzzjump.libgdxscreens.screen.StageUI;

import static com.fuzzjump.game.game.Assets.createCheckBoxStyle;
import static com.fuzzjump.game.game.Assets.createDefaultTBStyle;


public class SettingsUI extends StageUI {

    private MenuUI parent;
    private ScrollPane pane;

    public SettingsUI(MenuUI parent) {
        super(parent.getTextures(), parent.getGameSkin());
        this.stageScreen = parent.getStageScreen();
        this.parent = parent;
    }

    @Override
    public void init() {
        Label soundLabel = new Label("Sound:", getGameSkin(), "default");
        CheckBox soundCheckbox = new CheckBox("", createCheckBoxStyle(this));
        //soundCheckbox.setChecked(game.playingSound());
        TextButton logoutButton = new TextButton("Logout", createDefaultTBStyle(this));
        TextButton creditsButton = new TextButton("Credits", createDefaultTBStyle(this));
        TextButton backButton = new TextButton("Back", createDefaultTBStyle(this));

        Value padOuterSides = Value.percentWidth(.15f, this);

        Table container = new Table();
        container.setBackground(textures.getTextureRegionDrawable(Assets.UI_PANEL_WELCOME));

        Value padTopBottom = Value.percentHeight(.05f, container);
        Value buttonHeight = Value.percentHeight(.15f, container);
        Value buttonWidth = Value.percentWidth(.85f, container);

        container.add(soundLabel).left();
        container.add(soundCheckbox).right();

        container.row();
        container.add(logoutButton).size(buttonWidth, buttonHeight).colspan(2).padTop(padTopBottom).row();
        container.add(creditsButton).size(buttonWidth, buttonHeight).colspan(2).padTop(padTopBottom).row();
        container.add(backButton).size(buttonWidth, buttonHeight).colspan(2).padTop(padTopBottom);

        add(container).height(Value.percentHeight(.7f, this)).padLeft(padOuterSides).padRight(padOuterSides).expand().center();

        setFillParent(true);

        Helper.addClickAction(backButton, (e, x, y) -> backPressed());
        Helper.addClickAction(logoutButton, (e, x, y) -> logout());

        register(Assets.MenuUI.SOUND_TOGGLE, soundCheckbox);
        register(Assets.MenuUI.LOGOUT_BUTTON, logoutButton);
        register(Assets.MenuUI.CREDITS_BUTTON, creditsButton);
    }

    private void logout() {
        
    }


    @Override
    public void backPressed() {
        parent.showMain();
    }
}
