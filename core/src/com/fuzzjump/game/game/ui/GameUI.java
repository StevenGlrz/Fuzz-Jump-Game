package com.fuzzjump.game.game.ui;

import android.util.SparseArray;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.fuzzjump.game.game.StageIds;
import com.fuzzjump.game.game.StageUI;
import com.fuzzjump.game.game.screens.GameScreen;
import com.fuzzjump.game.model.SpecialType;
import com.fuzzjump.game.game.ingame.actors.Player;
import com.fuzzjump.game.model.map.GameMap;

import static com.fuzzjump.game.util.Styles.createDefaultTBStyle;
import static com.fuzzjump.game.util.Styles.createDialogStyle;

public class GameUI extends StageUI {

    private final GameScreen screen;

    private Table uiComponents;

    public GameUI(GameScreen screen) {
        this.screen = screen;
    }

    @Override
    public void init() {

        uiComponents = new Table();
        uiComponents.setFillParent(true);

        Label messageLabel = new Label("Loading", game.getSkin(), "default");
        final Image spinner = new Image(getTextureRegionDrawable("ui-progressspinner"));
        spinner.setOrigin(Align.center);
        spinner.addAction(Actions.forever(Actions.rotateBy(5f, .01f)));

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
        progressDialog.getContentTable().add(spinner).center().expand().size(Value.percentWidth(.25f, progressDialog));

        register(StageIds.GameUI.PROGRESS_DIALOG, progressDialog);
        register(StageIds.GameUI.PROGRESS_LABEL, messageLabel);
        register(StageIds.GameUI.PROGRESS_IMAGE, spinner);

    }



    /*private void drawSpecial(Table table, Batch batch, float alpha) {
        Player plr = screen.getPlayer();

        if (plr.getSpecials().getCurrentSpecial() != null) {
            if (plr.getSpecials().getCurrentDelay() > .001f) {
                if (System.currentTimeMillis() - lastSpecialFlip >= 25) {
                    lastSpecialFlip = System.currentTimeMillis();
                    SpecialType[] types = SpecialType.values();
                    //currentSpecial = game.getTextures().getTexture(types[screen.getRandom().nextInt(types.length)].icon);
                }
            } else {
                //currentSpecial = game.getTextures().getTexture(plr.getSpecials().getCurrentSpecial().icon);
            }
        } else {
            //currentSpecial = game.getTextures().getTexture("ui-question-mark");
        }

        float width = table.getWidth() / 2.5f;
        float height = width;
        float x = table.getWidth() / 2.3f - width / 2;
        float y = table.getHeight() / 1.75f - height / 2;
        batch.draw(currentSpecial, table.getX() + x, table.getY() + y, width, height);
    }*/


    @Override
    public void backPressed() {
    }

}
