package com.fuzzjump.game.game.screen.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Align;
import com.fuzzjump.game.game.Assets;
import com.fuzzjump.libgdxscreens.Textures;
import com.fuzzjump.libgdxscreens.screen.StageUI;

import javax.inject.Inject;

import static com.fuzzjump.game.game.Assets.createDialogStyle;

public class GameUI extends StageUI {


    private Table uiComponents;

    @Inject
    public GameUI(Textures textures, Skin skin) {
        super(textures, skin);
    }

    @Override
    public void init() {

        uiComponents = new Table();
        uiComponents.setFillParent(true);

        Label messageLabel = new Label("Loading", getGameSkin(), "default");
        final Image spinner = new Image(textures.getTextureRegionDrawable("ui-progressspinner"));
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

        register(Assets.GameUI.PROGRESS_DIALOG, progressDialog);
        register(Assets.GameUI.PROGRESS_LABEL, messageLabel);
        register(Assets.GameUI.PROGRESS_IMAGE, spinner);

    }


    /*private void drawSpecial(Table table, Batch batch, float alpha) {
        Player plr = screen.getPlayer();

        if (plr.getSpecials().getCurrentSpecial() != null) {
            if (plr.getSpecials().getCurrentDelay() > .001f) {
                if (System.currentTimeMillis() - lastSpecialFlip >= 25) {
                    lastSpecialFlip = System.currentTimeMillis();
                    SpecialType[] types = SpecialType.values();
                    //currentSpecial = game.getMapTextures().getTexture(types[screen.getRandom().nextInt(types.length)].icon);
                }
            } else {
                //currentSpecial = game.getMapTextures().getTexture(plr.getSpecials().getCurrentSpecial().icon);
            }
        } else {
            //currentSpecial = game.getMapTextures().getTexture("ui-question-mark");
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
