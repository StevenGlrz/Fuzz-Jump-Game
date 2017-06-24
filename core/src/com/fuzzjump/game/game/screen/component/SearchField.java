package com.fuzzjump.game.game.screen.component;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Created by Steven Galarza on 6/23/2017.
 */
public class SearchField extends TextField {

    private final Drawable search;

    public SearchField(Drawable search, String text, TextFieldStyle style) {
        super(text, style);
        this.search = search;
    }

    @Override
    public void draw(Batch batch, float alpha) {
        super.draw(batch, alpha);

        float errorDrawableSize = getHeight() * .5f;
        search.draw(batch, getX() + getWidth() - errorDrawableSize - getStyle().background.getRightWidth(), getY() + getHeight() / 2.0f - errorDrawableSize / 2f, errorDrawableSize, errorDrawableSize);
    }
}
