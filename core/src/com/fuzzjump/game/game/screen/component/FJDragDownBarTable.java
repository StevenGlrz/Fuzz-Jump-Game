package com.fuzzjump.game.game.screen.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
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
        titleBarTable.add(titleBarLabel = new Label("Welcome!", ui.getGameSkin(), "big")).padBottom(Value.percentHeight(.0175f, titleBarTable));
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