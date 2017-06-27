package com.fuzzjump.game.game.screen.component;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.fuzzjump.api.model.unlockable.Unlockable;
import com.fuzzjump.game.game.Assets;
import com.fuzzjump.game.game.player.Appearance;

public class CategoryFrame extends Table {

    private static final String NO_ITEM_TEXT = "N/A";

    private final int index;
    private final CheckBox.CheckBoxStyle style;
    private final Skin skin;
    private final BitmapFont font;
    private final String title;

    private TextureRegionDrawable categoryDrawable;
    private Unlockable unlockable;
    private Image checkboxImage;
    private boolean checked;

    private GlyphLayout glyphLayout = new GlyphLayout();

    public CategoryFrame(int index, Skin skin, CheckBox.CheckBoxStyle style) {
        this.index = index;
        this.title = Appearance.TITLES[index];
        this.font = skin.getFont(Assets.PROFILE_FONT);
        this.skin = skin;
        this.style = style;
    }

    public CategoryFrame init() {
        categoryDrawable = new TextureRegionDrawable();
        setTouchable(Touchable.enabled);
        checkboxImage = new Image(style.checkboxOff, Scaling.fit) {
            public void draw(Batch batch, float parentAlpha) {
                super.draw(batch, parentAlpha);
                drawCheckbox(batch);
            }
        };
        add(checkboxImage).expand().row();
        add(new Label(title, skin, "small"));
        return this;
    }

    private void drawCheckbox(Batch batch) {
        float width, height;
        float x, y;
        if (categoryDrawable != null) {
            width = checkboxImage.getImageWidth() * .75f;
            height = width / ((float) categoryDrawable.getRegion().getRegionWidth() / (float) categoryDrawable.getRegion().getRegionHeight());
            y = checkboxImage.getY() + checkboxImage.getImageY() + (checkboxImage.getImageHeight() / 2 - height / 2);
        } else {
            glyphLayout.setText(font, NO_ITEM_TEXT);
            width = glyphLayout.width;
            height = glyphLayout.height;
            y = checkboxImage.getY() + checkboxImage.getImageY() + (checkboxImage.getImageHeight() / 2 + height / 2);
        }
        x = checkboxImage.getX() + checkboxImage.getImageX() + (checkboxImage.getImageWidth() / 2 - width / 2);
        if (categoryDrawable != null) {
            categoryDrawable.draw(batch, x, y, width, height);
        } else {
            font.draw(batch, NO_ITEM_TEXT, x, y);
        }
    }

    public int getIndex() {
        return index;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        checkboxImage.setDrawable(checked ? style.checkboxOn : style.checkboxOff);
    }

    public void setCategoryDrawable(TextureRegion region) {
        if (region == null) {
            categoryDrawable = null;
        } else {
            if (categoryDrawable == null) {
                categoryDrawable = new TextureRegionDrawable(region);
            } else {
                categoryDrawable.setRegion(region);
            }
        }
    }

    public Unlockable getUnlockable() {
        return unlockable;
    }

    public void setUnlockable(Unlockable unlockable) {
        this.unlockable = unlockable;
    }
}
