package com.fuzzjump.game.game.ui.components;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class AutoVerticalGroup extends WidgetGroup {

    private Value padding, height;

	public AutoVerticalGroup(Value buttonPad, Value height) {
		super();
		this.padding = buttonPad;
        this.height = height;
	}

	@Override
	public void layout() {
		int count = getChildren().size;
        float actorHeight = height.get(null);
        float cellHeight = actorHeight + padding.get(null);
        float y = count * cellHeight;
        if (y != getHeight()) {
            setHeight(y);
            invalidateHierarchy();
        }
		for (int i = 0; i < count; i++) {
            y -= cellHeight;
			Actor actor = getChildren().get(i);
            actor.setY(y);
			actor.setX(0);
			actor.setWidth(getWidth());
		    actor.setHeight(height.get(null));
		}
		//super.layout();
	}

    @Override
    public float getPrefWidth () {
        return getWidth();
    }

    public float getPrefHeight () {
        return getHeight();
    }

}
