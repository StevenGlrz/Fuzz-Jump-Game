package com.fuzzjump.game.util;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.fuzzjump.game.game.event.GDXClickListener;

/**
 * Created by Steven Galarza on 6/16/2017.
 */
public class Helper {


    public static void addClickAction(TextButton button, GDXClickListener listener) {
        button.addListener(new ClickListener() {
           @Override
            public void clicked(InputEvent e, float x, float y) {
               listener.clicked(e, x, y);
           }
        });
    }
}
