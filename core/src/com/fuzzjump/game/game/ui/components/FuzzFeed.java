package com.fuzzjump.game.game.ui.components;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.fuzzjump.game.FuzzJump;
import com.fuzzjump.game.game.StageUI;
import com.fuzzjump.game.net.requests.FuzzFeedWebRequest;
import com.fuzzjump.game.util.Styles;

public class FuzzFeed {

	private List<FuzzFeedEntry> entries = new LinkedList<FuzzFeedEntry>();
	private int entryIdx = 0;

	public ClickListener listener = new ClickListener() {

		public void clicked(InputEvent e, float x, float y) {
			if (entries.size() == 0)
				return;
			float moveToX = 0;
			float moveFromX = 0;
			if (e.getListenerActor() == leftArrow) {
				entryIdx--;
				if (entryIdx < 0)
					entryIdx = entries.size() - 1;

				moveToX = feedImage.getWidth() * 1.5f;
				moveFromX = panelBg.getWidth() + feedImage.getWidth();

			} else if (e.getListenerActor() == rightArrow) {
				entryIdx++;
				moveToX = -panelBg.getWidth() + feedImage.getWidth();
				moveFromX = -feedImage.getWidth() * 1.5f;
			}

			final float finalMoveFrom = moveFromX;
			feedImage.addAction(Actions.sequence(Actions.moveTo(moveToX, feedImage.getY(), .1f), Actions.run(new Runnable() {
				@Override
				public void run() {
					feedImage.setX(finalMoveFrom);
					feedImage.addAction(Actions.moveTo(panelBg.getWidth() / 2 - feedImage.getWidth() / 2, feedImage.getY(), .1f));

					entryIdx %= entries.size();

					feedImage.getImage().setDrawable(entries.get(entryIdx).drawable);
				}

			})));
		}
	};

	private Table table;
	private ImageButton leftArrow;
	private ImageButton rightArrow;
	private ImageButton feedImage;
	private Table panelBg;

	public FuzzFeed(SecurePreferences preferences) {

	}

	public Table buildTable(final FuzzJump game, StageUI ui) {
		if (table != null)
			return table;
        FuzzFeedWebRequest request = new FuzzFeedWebRequest(this);
        request.connect(null);

		panelBg = new Table();
		panelBg.setBackground(ui.getTextureRegionDrawable("ui-banner"));

		Label label = new Label("Welcome to Fuzz Jump!", game.getSkin(), "profile");
		label.setAlignment(Align.center);

        feedImage = new ImageButton(ui.getTextureRegionDrawable("logo"));
        feedImage.getImage().setScaling(Scaling.fillY);
        feedImage.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Gdx.net.openURI(entries.get(entryIdx).link);
            }
        });

		panelBg.add(label).center().padTop(Value.percentWidth(.035f, panelBg));
		panelBg.row();
		panelBg.add(feedImage).center().size(Value.percentWidth(.75f, panelBg), Value.percentHeight(.5f, panelBg)).padTop(Value.percentWidth(.05f, panelBg)).padBottom(Value.percentWidth(.075f, panelBg));

		leftArrow = new ImageButton(Styles.createLeftBtnStyle(ui));
		rightArrow = new ImageButton(Styles.createRightBtnStyle(ui));

		leftArrow.addListener(listener);
		rightArrow.addListener(listener);

		table = new Table() {
			@Override
			public void layout() {
				super.layout();
				leftArrow.setX(leftArrow.getX() - leftArrow.getWidth() / 2);
				rightArrow.setX(rightArrow.getX() + rightArrow.getWidth() / 2);
			}
		};

		table.add(leftArrow).expand().left().size(Value.percentWidth(.15f, table));
		table.add(panelBg).size(Value.percentWidth(1f, table), Value.percentHeight(1f, table));
		table.add(rightArrow).expand().right().size(Value.percentWidth(.15f, table));

		panelBg.setZIndex(0);
		leftArrow.setZIndex(1);
		return table;
	}

    public void addPost(Texture image, String url) {
        entries.add(new FuzzFeedEntry(new TextureRegionDrawable(new TextureRegion(image)), url));
		feedImage.getStyle().imageUp = entries.get(0).drawable;
    }

	private class FuzzFeedEntry {

		private Drawable drawable;
		private String link;

		public FuzzFeedEntry(Drawable drawable, String link) {
			this.drawable = drawable;
			this.link = link;
		}
	}
}
