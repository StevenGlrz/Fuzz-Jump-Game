package com.fuzzjump.game.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.fuzzjump.game.game.StageUI;

/**
 * Created by stephen on 8/22/2015.
 */
public class SplashUI extends StageUI {

    @Override
    public void init() {
        setFillParent(true);
        setBackground(game.getSkin().getDrawable(UI_BACKGROUND));
        Image logo = new Image(getTextureRegionDrawable("logo"), Scaling.fit);
        Image ground = new Image(game.getSkin().getDrawable(UI_GROUND), Scaling.fillX, Align.bottom);
        add(logo).expand().center().size(Value.percentWidth(.5f, this), Value.percentWidth(.375f, this)).row();
        add(ground).bottom().width(Value.percentWidth(1f, this)).setActorY(0);
    }

    @Override
    public void backPressed() {

    }
}
