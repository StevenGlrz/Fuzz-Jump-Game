package com.fuzzjump.game.game.screen.component;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.List;

public class ActorSwitcher extends Table {

    private final List<ScreenView> actors = new ArrayList<>();
    private int currentIdx;

    public void setDisplayedChild(final int idx) {
        if (idx < 0 || idx > actors.size()) {
            return;
        }
        ScreenView view = actors.get(idx);
        Actor newActor = view.actor;
        if (actors.size() > 1) {
            clearChildren();
        }
        add(newActor).align(view.align).size(view.width, view.height).expand().fill();
        currentIdx = idx;
    }


    public void addWidget(Actor widget, Value width, Value height, int align) {
        actors.add(new ScreenView(widget, width, height, align));
        if (actors.size() == 1) {
            setDisplayedChild(0);
        }
    }


    public void addWidget(Actor widget, Value width, Value height) {
        addWidget(widget, width, height, Align.center);
    }

    public void addWidget(Actor widget) {
        addWidget(widget, Value.percentWidth(1f, this), Value.percentHeight(1f, this));
    }


    public void addWidget(Actor actor, float width, float height) {
        addWidget(actor, Value.percentWidth(width, this), Value.percentHeight(height, this));
    }

    public Actor getActor(int idx) {
        return actors.get(idx).actor;
    }

    public int getCurrentIdx() {
        return currentIdx;
    }

    private class ScreenView {

        public int align;
        public Actor actor;

        public Value width;
        public Value height;

        public ScreenView(Actor actor, Value width, Value height, int align) {
            this.align = align;
            this.actor = actor;
            this.width = width;
            this.height = height;
        }
    }
}
