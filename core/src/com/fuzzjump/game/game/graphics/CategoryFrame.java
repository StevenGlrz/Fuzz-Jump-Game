package com.fuzzjump.game.game.graphics;

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

/**
 * Created by Steveadoo on 9/30/2015.
 */
public class CategoryFrame extends Table {

    private static final String NO_ITEM_TEXT = "N/A";

    private final CheckBox.CheckBoxStyle style;
    private final Skin skin;
    private final BitmapFont font;
    private final String title;

    private TextureRegionDrawable categoryDrawable;
    private Image checkboxImage;
    private boolean checked;

    public CategoryFrame(String title, Skin skin, CheckBox.CheckBoxStyle style) {
        this.font = skin.getFont("profile-font");
        this.title = title;
        this.skin = skin;
        this.style = style;
        this.categoryDrawable = new TextureRegionDrawable();
        setTouchable(Touchable.enabled);
        init();
    }

    public void init() {
        checkboxImage = new Image(style.checkboxOff, Scaling.fit) {
            public void draw(Batch batch, float parentAlpha) {
                super.draw(batch, parentAlpha);
                drawCheckbox(batch, parentAlpha);
            }
        };
        add(checkboxImage).expand().row();
        add(new Label(title, skin, "small"));
    }

    private GlyphLayout glyphLayout = new GlyphLayout();

    private void drawCheckbox(Batch batch, float parentAlpha) {
        float width = 0;
        float height = 0;
        float x = 0;
        float y = 0;
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
            if (categoryDrawable == null)
                categoryDrawable = new TextureRegionDrawable(region);
            else
                categoryDrawable.setRegion(region);
        }
    }

}
