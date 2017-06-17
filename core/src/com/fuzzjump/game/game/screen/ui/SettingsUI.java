package com.fuzzjump.game.game.screen.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.fuzzjump.game.game.Assets;
import com.fuzzjump.game.util.Helper;
import com.fuzzjump.libgdxscreens.StageUI;

import static com.fuzzjump.game.game.Assets.createCheckBoxStyle;
import static com.fuzzjump.game.game.Assets.createDefaultTBStyle;

/**
 * Kerpow Games, LLC
 * Created by stephen on 4/8/2015.
 */
public class SettingsUI extends StageUI {

    private MenuUI parent;
    private ScrollPane pane;


    public SettingsUI(MenuUI parent) {
        super(parent.getTextures(), parent.getSkin());
        this.stageScreen = parent.getStageScreen();
        this.parent = parent;
    }

    @Override
    public void init() {
        Label soundLabel = new Label("Sound", getSkin(), "default");
        CheckBox soundCheckbox = new CheckBox("", createCheckBoxStyle(this));
        //soundCheckbox.setChecked(game.playingSound());
        TextButton logoutButton = new TextButton("Logout", createDefaultTBStyle(this));
        TextButton creditsButton = new TextButton("Credits", createDefaultTBStyle(this));
        TextButton backButton = new TextButton("Back", createDefaultTBStyle(this));

        Table container = new Table();
        container.setBackground(textures.getTextureRegionDrawable(Assets.UI_PANEL_WELCOME));

        Value padSidesFirstRow = Value.percentWidth(.05f, container);
        Value padSides = Value.percentWidth(.025f, container);

        Value padTopBottom = Value.percentHeight(.05f, container);
        Value buttonHeight = Value.percentHeight(.175f, container);
        Value checkboxSize = Value.percentHeight(.1f, container);

        container.add(soundLabel).padLeft(padSidesFirstRow).left();
        container.add(soundCheckbox).size(checkboxSize).padRight(padSidesFirstRow).right();
        container.row();
        container.add(logoutButton).height(buttonHeight).colspan(2).padLeft(padSides).padRight(padSides).padTop(padTopBottom).center();
        container.row();
        container.add(creditsButton).height(buttonHeight).colspan(2).padLeft(padSides).padRight(padSides).padTop(padTopBottom).center();
        container.row();
        container.add(backButton).height(buttonHeight).colspan(2).padLeft(padSides).padRight(padSides).padTop(padTopBottom).center();

        add(container).height(Value.percentHeight(.65f, this)).width(Value.percentWidth(1f, this)).center();
        add(new Actor()).expand();

        setFillParent(true);

        Helper.addClickAction(backButton, (e, x, y) -> backPressed());

        register(Assets.MenuUI.SOUND_TOGGLE, soundCheckbox);
        register(Assets.MenuUI.LOGOUT_BUTTON, logoutButton);
        register(Assets.MenuUI.CREDITS_BUTTON, creditsButton);
    }


    @Override
    public void backPressed() {
        parent.showMain();
    }
}
