package com.fuzzjump.game.game.screen.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Scaling;
import com.fuzzjump.game.game.Assets;
import com.fuzzjump.game.game.player.Profile;
import com.fuzzjump.libgdxscreens.screen.StageUI;
import com.fuzzjump.libgdxscreens.screen.StageUITextures;
import com.fuzzjump.libgdxscreens.graphics.component.DragDownBarTable;

public class FJDragDownBarTable extends DragDownBarTable {

    private final StageUI ui;

    private final Profile profile;

    private Label titleBarLabel;
    private Label rankingLabel;
    private Label coinsLabel;

    private int currentRanking;
    private int currentCoins;

    public FJDragDownBarTable(StageUI ui) {
        this(ui, null);
    }

    public FJDragDownBarTable(StageUI ui, Profile profile) {
        super(null, null, null, profile == null);
        this.ui = ui;
        this.profile = profile;
        populate();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if (currentRanking != profile.getLevel()) {
            currentRanking = profile.getLevel();
            rankingLabel.setText(String.valueOf(currentRanking));
        }
        if (currentCoins != profile.getCoins()) {
            currentCoins = profile.getCoins();
            coinsLabel.setText(String.valueOf(currentCoins));
        }
    }

    public void populate() {
        StageUITextures textures = ui.getTextures();

        setBackground(textures.getTextureRegionDrawable("uibackground"));
        titleBarTable.setBackground(textures.getTextureRegionDrawable(profile == null ? "toppanelnodrag" : "toppanel"));
        dragDownTable.setBackground(new ColorDrawable(Color.valueOf("73BB44"), 1f, 1f));
        if (profile == null) {
            titleBarTable.add(new Label("Welcome!", ui.getSkin(), "big")).padBottom(Value.percentHeight(.0175f, titleBarTable));
        } else {
            Value padBottom = Value.percentHeight(.225f, titleBarTable);

            Value leftRightWidth = Value.percentWidth(0.2532679738562092f, titleBarTable);

            Table leftTable = new Table();
            Table rightTable = new Table();
            Value imageHeight = Value.percentHeight(.65f, leftTable);
            Value badgeWidth = imageHeight;

            titleBarTable.defaults().padBottom(padBottom);
            titleBarTable.add(leftTable).size(leftRightWidth, Value.percentHeight(.65f, titleBarTable));
            titleBarTable.add(new Label(profile.getDisplayName(), ui.getSkin(), "big")).padBottom(Value.percentHeight(.0175f, titleBarTable)).center().expand();
            titleBarTable.add(rightTable).size(leftRightWidth, Value.percentHeight(.65f, titleBarTable));

            Value padSides = Value.percentWidth(.05f, leftTable);

            leftTable.add(new Image(ui.getTextures().getTextureRegionDrawable("level-badge"), Scaling.fit)).size(badgeWidth, imageHeight).padLeft(padSides).center().left();
            leftTable.add(rankingLabel = new Label(String.valueOf(profile.getLevel()), ui.getSkin(), "default")).width(Value.percentWidth(.5f, leftTable)).center().expand().left();
            rightTable.add(coinsLabel = new Label(String.valueOf(profile.getCoins()), ui.getSkin(), "default")).width(Value.percentWidth(.5f, rightTable)).center().expand().right();
            rightTable.add(new Image(ui.getTextures().getTextureRegionDrawable(Assets.UI_KERPOW_COIN), Scaling.fit)).size(imageHeight, imageHeight).padRight(padSides).center().right();

            currentCoins = profile.getCoins();
            currentRanking = profile.getLevel();
        }
    }

    /**
     * Update the upper menu with new profile information.
     * This updates all the bar elements only if there was a profile to begin with.
     * @param profile The profile.
     */
    public void update(Profile profile) {
        if (rankingLabel != null) {
            rankingLabel.setText(String.valueOf(profile.getLevel()));
            coinsLabel.setText(String.valueOf(profile.getCoins()));
        }
    }
}