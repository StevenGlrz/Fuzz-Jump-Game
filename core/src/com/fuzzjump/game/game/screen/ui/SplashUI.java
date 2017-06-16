package com.fuzzjump.game.game.screen.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.fuzzjump.game.game.Assets;
import com.fuzzjump.libgdxscreens.StageUI;
import com.fuzzjump.libgdxscreens.Textures;

import javax.inject.Inject;

/**
 * Created by Steven Galarza on 6/15/2017.
 */
public class SplashUI extends StageUI {

    @Inject
    public SplashUI(Textures textures, Skin skin) {
        super(textures, skin);
    }

    @Override
    public void init() {
    }

    @Override
    public void backPressed() {

    }

    public void drawSplash() {
        setFillParent(true);
        setBackground(skin.getDrawable(Assets.UI_BACKGROUND));

        Image logo = new Image(textures.getTextureRegionDrawable(Assets.UI_LOGO), Scaling.fit);
        Image ground = new Image(skin.getDrawable(Assets.UI_GROUND), Scaling.fillX, Align.bottom);

        add(logo).expand().center().size(Value.percentWidth(.5f, this), Value.percentWidth(.375f, this)).row();
        add(ground).bottom().width(Value.percentWidth(1f, this)).setActorY(0);
    }
}