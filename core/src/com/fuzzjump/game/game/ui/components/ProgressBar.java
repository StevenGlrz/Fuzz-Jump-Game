package com.fuzzjump.game.game.ui.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Created by stephen on 12/11/2014.
 */
public class ProgressBar extends com.badlogic.gdx.scenes.scene2d.ui.ProgressBar {

    public ProgressBar(float min, float max, float stepSize, boolean vertical, Skin skin, String style) {
        super(min, max, stepSize, vertical, skin, style);
    }

    @Override
    public void draw(Batch batch, float alpha) {
        //super.draw(batch, alpha);
        ProgressBarStyle style = getStyle();
        TextureRegionDrawable bg = (TextureRegionDrawable) style.background;
        TextureRegionDrawable knob = (TextureRegionDrawable) style.knob;
        float ratio = getVisualValue() / getMaxValue();
        float xOffset = getWidth() * ratio;
        float bgWidth = getWidth() - xOffset;
        int bgX = (int) (bg.getRegion().getRegionWidth() * ratio);
        //bg.draw(batch, getX(), getY(), getWidth(), getHeight());
        batch.draw(bg.getRegion().getTexture(), getX() + xOffset, getY(), getWidth(), getHeight(), bgX, 0, bg.getRegion().getRegionWidth(), bg.getRegion().getRegionHeight(), false, false);
        batch.draw(knob.getRegion().getTexture(), getX(), getY(), xOffset, getHeight(), 0, 0, (int) (knob.getRegion().getRegionWidth() * ratio), knob.getRegion().getRegionHeight(), false, false);
    }

}
