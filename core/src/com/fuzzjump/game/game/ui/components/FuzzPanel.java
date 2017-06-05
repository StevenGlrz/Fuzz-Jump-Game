package com.fuzzjump.game.game.ui.components;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

public class FuzzPanel extends Table {

    private boolean moveDown = true;
    private Table contentTable, bottomTable;

	private Skin skin;

	public FuzzPanel(boolean moveDown, Drawable background) {
        super();
        this.moveDown = moveDown;
		this.setBackground(background);
        init();
    }

	private void init() {
		contentTable = new Table();
		bottomTable = new Table();
		Value pad = Value.percentWidth(.15f, this);
		add(contentTable).center().expand().size(Value.percentWidth(.85f, this), Value.percentWidth(.7f, this)).pad(pad);
		row();
		add(bottomTable).bottom().size(Value.percentWidth(.85f, this), pad).padLeft(pad).padRight(pad);
	}
	
	public Table getContentTable() {
		return contentTable;
	}
	
	public void addButtons(ImageButton[] buttons, boolean middleBig) {
		boolean even = buttons.length % 2 == 0;
        if (!middleBig) even = true;
		float totalPad = buttons.length * .1f;
		Value pad = Value.percentWidth(.05f, bottomTable);
		for (int i = 0; i < buttons.length; i++) {
			if (even) {
				//this is useless.
				bottomTable.add(buttons[i]).align(Align.center).size(Value.percentWidth((1f - totalPad) / ((buttons.length - 1) * 1.5f), bottomTable)).padLeft(pad).padRight(pad);
			} else {
				if (i == buttons.length / 2) {
					bottomTable.add(buttons[i]).align(Align.center).size(Value.percentWidth((1f - totalPad) / ((buttons.length - 1) * 1.5f) + .1f, bottomTable)).padLeft(pad).padRight(pad);;
				} else {
					bottomTable.add(buttons[i]).align(Align.center).size(Value.percentWidth((1f - totalPad) / ((buttons.length - 1) * 1.5f), bottomTable)).padLeft(pad).padRight(pad);
				}
			}
		}
	}
	
	@Override
	public void layout() {
		super.layout();
        if (moveDown)
            contentTable.setY(contentTable.getY() - bottomTable.getHeight() / 3);
		bottomTable.setY(bottomTable.getY() - bottomTable.getHeight() / 3);
	}

	public Table getBottomTable() {
		return bottomTable;
	}
	
	private class FuzzFeedRequest {
		
	}
}
