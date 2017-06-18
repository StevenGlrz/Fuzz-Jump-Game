package com.fuzzjump.game.game.screen.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Scaling;
import com.fuzzjump.game.game.player.Profile;
import com.fuzzjump.libgdxscreens.StageUI;
import com.fuzzjump.libgdxscreens.StageUITextures;

public class FJDragDownBarTable extends DragDownBarTable {

    private final StageUI ui;

    private final Profile profile;

    private Label titleBarLabel;
    private Label levelLabel;
    private Label coinsLabel;

    public FJDragDownBarTable(StageUI ui) {
        this(ui, null);
    }

    public FJDragDownBarTable(StageUI ui, Profile profile) {
        super(null, null, null, true);
        this.ui = ui;
        this.profile = profile;
        populate();
    }

    public void populate() {
        StageUITextures textures = ui.getTextures();

        setBackground(textures.getTextureRegionDrawable("uibackground"));
        titleBarTable.setBackground(textures.getTextureRegionDrawable("toppanelnodrag"));
        dragDownTable.setBackground(new ColorDrawable(Color.valueOf("73BB44"), 1f, 1f));
        if (profile == null) {
            titleBarTable.add(titleBarLabel = new Label("Welcome!", ui.getSkin(), "big")).padBottom(Value.percentHeight(.0175f, titleBarTable));
        } else {
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

            leftTable.add(new Image(ui.getTextures().getTextureRegionDrawable("level-badge"), Scaling.fit)).size(badgeWidth, imageHeight).padLeft(padSides).center().left();
            leftTable.add(levelLabel = new Label(String.valueOf(profile.getRanking()), ui.getSkin(), "default")).width(Value.percentWidth(.5f, leftTable)).center().expand().left();
            rightTable.add(coinsLabel = new Label(String.valueOf(profile.getCoins()), ui.getSkin(), "default")).width(Value.percentWidth(.5f, rightTable)).center().expand().right();
            rightTable.add(new Image(ui.getTextures().getTextureRegionDrawable("kerpow-coin"), Scaling.fit)).size(imageHeight, imageHeight).padRight(padSides).center().right();
        }
    }

    public void setTitle(String title) {
        this.titleBarLabel.setText(title);
    }

//    @Override
//    public void profileChanged() {
//        titleBarLabel.setText(profile.getName());
//        levelLabel.setText(String.valueOf(profile.getRanking()));
//        coinsLabel.setText(String.valueOf(profile.getCoins()));
//    }
}