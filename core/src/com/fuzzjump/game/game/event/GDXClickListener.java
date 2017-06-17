package com.fuzzjump.game.game.event;

import com.badlogic.gdx.scenes.scene2d.InputEvent;

/**
 * Created by Steven Galarza on 6/16/2017.
 */
@FunctionalInterface
public interface GDXClickListener {
    void clicked(InputEvent event, float x, float y);
}
