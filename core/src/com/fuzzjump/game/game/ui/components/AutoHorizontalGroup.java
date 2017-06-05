package com.fuzzjump.game.game.ui.components;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Value;

public class AutoHorizontalGroup extends HorizontalGroup {

    private boolean fillHeight;
    private Value padding;

	public AutoHorizontalGroup(Value buttonPad, boolean fillHeight) {
		super();
		this.padding = buttonPad;
        this.fillHeight = fillHeight;
	}
	
	@Override
	public void layout() {
		int count = getChildren().size;
        float actorWidth = (getWidth() - (padding.get(null) * 2 * count)) / count;
        float cellWidth = actorWidth + (padding.get(null) * 2);
        float x = (getWidth() / 2.0f) - (cellWidth * (count / 2.0f));
		for (int i = 0; i < count; i++) {
			Actor actor = getChildren().get(i);
			actor.setX(x + padding.get(null));
			actor.setWidth(actorWidth);
		    actor.setHeight(fillHeight ? getHeight() : actorWidth);
            x += cellWidth;
		}
		//super.layout();
	}
}
