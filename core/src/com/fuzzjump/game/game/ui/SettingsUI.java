package com.fuzzjump.game.game.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.fuzzjump.game.FuzzJump;
import com.fuzzjump.game.game.StageIds;
import com.fuzzjump.game.game.StageScreen;
import com.fuzzjump.game.game.StageUI;
import com.fuzzjump.game.model.character.Unlockable;
import com.fuzzjump.game.model.character.UnlockableDefinition;

import static com.fuzzjump.game.util.Styles.*;

/**
 * Kerpow Games, LLC
 * Created by stephen on 4/8/2015.
 */
public class SettingsUI extends StageUI {

    private MenuUI parent;
    private ScrollPane pane;

    public SettingsUI(StageScreen stage, FuzzJump game, MenuUI parent) {
        this.game = game;
        this.stageScreen = stage;
        this.parent = parent;
    }


    @Override
    public TextureRegionDrawable getTextureRegionDrawable(String name) {
        return parent.getTextureRegionDrawable(name);
    }

    @Override
    public TextureRegion getTexture(String name) {
        return parent.getTexture(name);
    }

    @Override
    public TextureRegion getColored(UnlockableDefinition definition, int colorIndex, boolean hardref) {
        return parent.getColored(definition, colorIndex, hardref);
    }

    @Override
    public TextureRegion getColored(Unlockable unlockable, boolean hardref) {
        return parent.getColored(unlockable, hardref);
    }

    @Override
    public void init() {
        Label soundLabel = new Label("Sound...............................", game.getSkin(), "default");
        CheckBox soundCheckbox = new CheckBox("", createCheckBoxStyle(this));
        soundCheckbox.setChecked(game.playingSound());
        TextButton logoutButton = new TextButton("Logout", createDefaultTBStyle(this));
        TextButton creditsButton = new TextButton("Credits", createDefaultTBStyle(this));
        TextButton backButton = new TextButton("Back", createDefaultTBStyle(this));

        Table container = new Table();
        container.setBackground(getTextureRegionDrawable("panel"));

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

        backButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                backPressed();
            }
        });

        register(StageIds.MenuUI.SOUND_TOGGLE, soundCheckbox);
        register(StageIds.MenuUI.LOGOUT_BUTTON, logoutButton);
        register(StageIds.MenuUI.CREDITS_BUTTON, creditsButton);
    }


    @Override
    public void backPressed() {
        parent.showMain();
    }
}
