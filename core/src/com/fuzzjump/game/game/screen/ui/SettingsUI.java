package com.fuzzjump.game.game.screen.ui;

import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.fuzzjump.game.game.Assets;
import com.fuzzjump.game.game.screen.MenuScreen;
import com.fuzzjump.game.io.FuzzPersistence;
import com.fuzzjump.game.util.Helper;
import com.fuzzjump.libgdxscreens.screen.StageUI;

import static com.fuzzjump.game.game.Assets.createCheckBoxStyle;
import static com.fuzzjump.game.game.Assets.createDefaultTBStyle;


public class SettingsUI extends StageUI {

    private final MenuUI parent;
    private final FuzzPersistence persistence;

    public SettingsUI(MenuUI parent, FuzzPersistence persistence) {
        super(parent.getTextures(), parent.getGameSkin());
        this.stageScreen = parent.getStageScreen();
        this.parent = parent;
        this.persistence = persistence;
    }

    @Override
    public void init() {
        Label soundLabel = new Label("Sound:", getGameSkin(), "default");
        CheckBox soundCheckbox = new CheckBox("", createCheckBoxStyle(this));

        soundCheckbox.setChecked(persistence.isSoundSettingSet());
        TextButton logoutButton = new TextButton("Logout", createDefaultTBStyle(this));
        TextButton backButton = new TextButton("Back", createDefaultTBStyle(this));

        Value padOuterSides = Value.percentWidth(.15f, this);

        Table container = new Table();
        container.setBackground(textures.getTextureRegionDrawable(Assets.UI_PANEL_WELCOME));

        Value padTopBottom = Value.percentHeight(.05f, container);
        Value padTopOption = Value.percentHeight(.25f, container);
        Value buttonHeight = Value.percentHeight(.15f, container);
        Value buttonWidth = Value.percentWidth(.85f, container);

        container.add(soundLabel).center().padBottom(padTopOption).left();
        container.add(soundCheckbox).top().padBottom(padTopOption).right();

        container.row();
        container.add(logoutButton).size(buttonWidth, buttonHeight).colspan(2).padTop(padTopBottom).row();
        container.add(backButton).size(buttonWidth, buttonHeight).colspan(2).padTop(padTopBottom);

        add(container).height(Value.percentHeight(.6f, this)).padLeft(padOuterSides).padRight(padOuterSides).expand().center();

        setFillParent(true);

        Helper.addClickAction(backButton, (e, x, y) -> backPressed());
        Helper.addClickAction(logoutButton, (e, x, y) -> {
            MenuScreen menuScreen = (MenuScreen) stageScreen;
            menuScreen.logout();
        });
        Helper.addClickAction(soundCheckbox, (e, x, y) -> {
            persistence.setSoundSetting(soundCheckbox.isChecked());
        });

        register(Assets.MenuUI.SOUND_TOGGLE, soundCheckbox);
        register(Assets.MenuUI.LOGOUT_BUTTON, logoutButton);
    }


    @Override
    public void backPressed() {
        parent.showMain();
    }
}
