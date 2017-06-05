package com.fuzzjump.game.game.ui.components;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class TabbedGroup extends Table {

	private ArrayList<Table> tables;
	private HorizontalGroup buttons;
	private Skin skin;

	public TabbedGroup(Skin skin) {
		super();
		this.tables = new ArrayList<Table>();
		this.buttons = new HorizontalGroup() {
			@Override
			public void layout() {
				int count = buttons.getChildren().size;
				float width = getWidth() / count + 1;
				for (int i = 0; i < count; i++) {
					Actor actor = buttons.getChildren().get(i);
					actor.setX(i * width);
					actor.setWidth(width);
					actor.setHeight(getHeight());
				}
				//super.layout();
			}
		};
		this.skin = skin;
		init();
	}
	
	private void init() {
		buttons.align(Align.center);
		buttons.fill();
		add(buttons).size(Value.percentWidth(1f, this), Value.percentHeight(.10f, this)).top().row();
	}

	public void addTab(String name, Table content) {
		content.setBackground(skin.getDrawable("tab_content_bg"));
		tables.add(content);
		
		final TextButton button = new TextButton(name, skin, "tab");
		buttons.addActor(button);
		
		button.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				TabbedGroup.this.removeActor(TabbedGroup.this.getChildren().get(1));
				TabbedGroup.this.add(tables.get(indexOf(button))).expand();
			}
			
		});
		
		if (tables.size() == 1) {
			add(content).expand();
		}
	}

	protected int indexOf(TextButton button) {
		int idx = 0; //imsolazybruh
		for (Actor actor : buttons.getChildren()) {
			if (actor == button) {
				return idx;
			}
			idx++;
		}
		return -1; //watzefuckisg09nonbruh
	}
}
