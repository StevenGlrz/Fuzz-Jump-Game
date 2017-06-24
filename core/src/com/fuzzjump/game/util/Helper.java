package com.fuzzjump.game.util;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.fuzzjump.game.game.event.GDXClickListener;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.w3c.dom.Element;

/**
 * Created by Steven Galarza on 6/16/2017.
 */
public class Helper {

    public static String getNodeValue(Element element, String childTag) {
        return element.getElementsByTagName(childTag).item(0).getTextContent();
    }

    public static void addClickAction(Actor actor, GDXClickListener listener) {
        actor.addListener(new ClickListener() {
           @Override
            public void clicked(InputEvent e, float x, float y) {
               listener.clicked(e, x, y);
           }
        });
    }

    public static JsonObject getJsonObject(JsonElement element) {
        return element.isJsonNull() ? null : element.getAsJsonObject();
    }

    public static <T> T fallback(T value, T fallback) {
        return value == null ? fallback : value;
    }
}
