package com.fuzzjump.game.game.screen.component;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;

/**
 * Created by Steven Galarza on 6/17/2017.
 */
public class FuzzDialog extends Dialog {

    private final float widthMod;
    private final float heightMod;

    public FuzzDialog(String title, WindowStyle windowStyle, float widthMod, float heightMod) {
        super(title, windowStyle);
        this.widthMod = widthMod;
        this.heightMod = heightMod;
    }

    @Override
    public float getPrefWidth() {
        return Gdx.graphics.getWidth() * widthMod;
    }

    @Override
    public float getPrefHeight() {
        return Gdx.graphics.getWidth() * heightMod;
    }
}
