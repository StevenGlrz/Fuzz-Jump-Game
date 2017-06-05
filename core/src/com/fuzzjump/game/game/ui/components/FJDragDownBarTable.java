package com.fuzzjump.game.game.ui.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Scaling;
import com.fuzzjump.game.game.StageUI;
import com.fuzzjump.game.model.profile.Profile;
import com.fuzzjump.game.util.ColorDrawable;

public class FJDragDownBarTable extends DragDownBarTable implements Profile.ProfileChangeEventListener {

    private final Profile profile;
    private final StageUI ui;
    private Label titleBarLabel;
    private Label levelLabel;
    private Label coinsLabel;

    public FJDragDownBarTable(StageUI ui, Profile profile) {
        super(null, null, null, profile == null);
        this.ui = ui;
        this.profile = profile;
        populate();
    }

    public void populate() {
        setBackground(ui.getTextureRegionDrawable("uibackground"));
        titleBarTable.setBackground(ui.getTextureRegionDrawable(profile == null ? "toppanelnodrag" : "toppanel"));
        dragDownTable.setBackground(new ColorDrawable(Color.valueOf("73BB44"), 1f, 1f));
        if (profile == null) {
            titleBarTable.add(titleBarLabel = new Label("Welcome!", ui.getSkin(), "big")).padBottom(Value.percentHeight(.0175f, titleBarTable));
        } else {
            profile.addChangeListener(this);
            Value padBottom = Value.percentHeight(.225f, titleBarTable);

            Value leftRightWidth = Value.percentWidth(0.2532679738562092f, titleBarTable);

            Table leftTable = new Table();
            Table rightTable = new Table();
            Value imageHeight = Value.percentHeight(.65f, leftTable);
            Value badgeWidth = imageHeight;

            titleBarTable.defaults().padBottom(padBottom);
            titleBarTable.add(leftTable).size(leftRightWidth, Value.percentHeight(.65f, titleBarTable));
            titleBarTable.add(titleBarLabel = new Label(profile.getName(), ui.getSkin(), "big")).padBottom(Value.percentHeight(.0175f, titleBarTable)).center().expand();
            titleBarTable.add(rightTable).size(leftRightWidth, Value.percentHeight(.65f, titleBarTable));

            Value padSides = Value.percentWidth(.05f, leftTable);

            leftTable.add(new Image(ui.getTextureRegionDrawable("level-badge"), Scaling.fit)).size(badgeWidth, imageHeight).padLeft(padSides).center().left();
            leftTable.add(levelLabel = new Label(String.valueOf(profile.getRanking()), ui.getSkin(), "default")).width(Value.percentWidth(.5f, leftTable)).center().expand().left();
            rightTable.add(coinsLabel = new Label(String.valueOf(profile.getCoins()), ui.getSkin(), "default")).width(Value.percentWidth(.5f, rightTable)).center().expand().right();
            rightTable.add(new Image(ui.getTextureRegionDrawable("kerpow-coin"), Scaling.fit)).size(imageHeight, imageHeight).padRight(padSides).center().right();
        }
    }

    public void setTitle(String title) {
        this.titleBarLabel.setText(title);
    }

    @Override
    public void profileChanged() {
        titleBarLabel.setText(profile.getName());
        levelLabel.setText(String.valueOf(profile.getRanking()));
        coinsLabel.setText(String.valueOf(profile.getCoins()));
    }
}