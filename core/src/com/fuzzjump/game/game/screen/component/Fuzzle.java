package com.fuzzjump.game.game.screen.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.fuzzjump.api.model.unlockable.Unlockable;
import com.fuzzjump.game.game.player.Appearance;
import com.fuzzjump.game.game.player.Profile;
import com.fuzzjump.game.game.player.unlockable.UnlockableColorizer;
import com.fuzzjump.game.game.player.unlockable.UnlockableDefinition;
import com.fuzzjump.libgdxscreens.screen.ScreenLoader;
import com.fuzzjump.libgdxscreens.screen.StageUI;

public class Fuzzle extends Actor {

    private final UnlockableColorizer colorizer;

    private TextureRegionDrawable questionMark;
    private Profile profile;
    private StageUI ui;


    private Unlockable[] appearance;
    private Rectangle[] appBounds;
    private TextureRegionDrawable[] drawables;

    private boolean drawFrame = true;

    public Fuzzle(StageUI ui, UnlockableColorizer colorizer) {
        super();
        this.ui = ui;
        this.colorizer = colorizer;

        this.questionMark = ui.getTextures().getTextureRegionDrawable("ui-question-mark");

        this.appearance = new Unlockable[Appearance.COUNT];
        this.appBounds = new Rectangle[Appearance.COUNT];
        this.drawables = new TextureRegionDrawable[Appearance.COUNT];

        for (int i = 0; i < Appearance.COUNT; i++) {
            drawables[i] = new TextureRegionDrawable();
            appBounds[i] = new Rectangle();
        }
    }

    public Fuzzle(StageUI ui, UnlockableColorizer colorizer, Profile profile) {
        this(ui, colorizer);
        setProfile(profile);
    }

    public Fuzzle(StageUI ui, UnlockableColorizer colorizer, Profile profile, boolean drawFrame) {
        this(ui, colorizer, profile);
        this.drawFrame = drawFrame;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Fuzzle load(ScreenLoader loader) {
        if (profile != null) {
            for (int i = 0; i < Appearance.COUNT; i++) {
                final int index = i;
                final Unlockable equippedUnlockable = profile.getAppearance().getEquip(index);
                if (appearance[i] != equippedUnlockable) { // Don't update if nothing has changed
                    if (i == Appearance.FUZZLE) {
                        setAppearance(index, equippedUnlockable); // fuzzle's are cached, no need to defer loading
                    } else {
                        loader.add(() -> setAppearance(index, equippedUnlockable));
                    }
                }
            }
        }
        return this;
    }

    @Override
    public void sizeChanged() {
        super.sizeChanged();
        update();
    }

    public void update() {
        if (appearance[Appearance.FUZZLE] == null || drawables[Appearance.FUZZLE] == null) {
            return;
        }
        updateFuzzleBounds();

        for (int i = Appearance.HEAD; i < Appearance.COUNT; i++) {
            Unlockable unlockable = appearance[i];
            if (unlockable != null) {
                updateBounds(appBounds[i], colorizer.definitionFor(unlockable), drawables[i]);
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegionDrawable fuzzleDrawable = drawables[Appearance.FUZZLE];
        TextureRegionDrawable frameDrawable = drawables[Appearance.FRAME];

        Rectangle fuzzleBounds = appBounds[Appearance.FUZZLE];

        batch.setColor(Color.WHITE);
        super.draw(batch, parentAlpha);
        if (frameDrawable.getRegion() == null || fuzzleDrawable.getRegion() == null) {
            float questionMarkWidth = getWidth() * .85f;
            float questionMarkHeight = questionMarkWidth / (questionMark.getRegion().getRegionWidth() / questionMark.getRegion().getRegionHeight());
            questionMark.draw(batch, getX() + getWidth() / 2 - questionMarkWidth / 2, getY() + getHeight() / 2 - questionMarkHeight / 2, questionMarkWidth, questionMarkHeight);
            return;
        }
        if (drawFrame) {
            frameDrawable.draw(batch, getX(), getY(), getWidth(), getHeight());
        }
        fuzzleDrawable.draw(batch, getX() + fuzzleBounds.x, getY() + fuzzleBounds.y, fuzzleBounds.width, fuzzleBounds.height);
        drawPart(batch, Appearance.FACE);
        drawPart(batch, Appearance.EYES);
        drawPart(batch, Appearance.HEAD);
    }

    public void drawPart(Batch batch, int index) {
        Unlockable unlockable = appearance[index];
        Rectangle bounds = appBounds[index];
        TextureRegionDrawable drawable = drawables[index];

        if (unlockable == null || bounds.width == 0 || bounds.height == 0)
            return;
        drawable.draw(batch, getX() + bounds.x, getY() + bounds.y, bounds.width, bounds.height);
    }

    public void updateFuzzleBounds() {
        Rectangle fuzzleBounds = appBounds[Appearance.FUZZLE];
        TextureRegionDrawable fuzzleDrawable = drawables[Appearance.FUZZLE];

        float fuzzleWidth = drawFrame ? getWidth() * .85f : getWidth();
        float fuzzleHeight = fuzzleWidth / (fuzzleDrawable.getRegion().getRegionWidth() / fuzzleDrawable.getRegion().getRegionHeight());
        fuzzleBounds.x = getWidth() / 2 - fuzzleWidth / 2;
        fuzzleBounds.y = getHeight() / 2 - fuzzleHeight / 2;
        fuzzleBounds.width = fuzzleWidth;
        fuzzleBounds.height = fuzzleHeight;
    }

    public void updateBounds(Rectangle bounds, UnlockableDefinition definition, TextureRegionDrawable drawable) {
        try {
            if (definition.getBounds() == null) {
                return;
            }
            Rectangle fuzzleBounds = appBounds[Appearance.FUZZLE];
            Rectangle uBound = definition.getBounds()[appearance[Appearance.FUZZLE].getDefinitionId()];
            bounds.x = fuzzleBounds.x + (uBound.x * fuzzleBounds.width);
            bounds.y = fuzzleBounds.y + (uBound.y * fuzzleBounds.height);
            if (uBound.height == -1) {
                bounds.width = fuzzleBounds.width * uBound.width;
                bounds.height = bounds.width / ((float) drawable.getRegion().getRegionWidth() / drawable.getRegion().getRegionHeight());
            } else if (uBound.width == -1) {
                bounds.height = fuzzleBounds.height * uBound.height;
                bounds.width = bounds.height * ((float) drawable.getRegion().getRegionWidth() / drawable.getRegion().getRegionHeight());
            } else {
                bounds.width = fuzzleBounds.width * uBound.width;
                bounds.height = fuzzleBounds.height * uBound.height;
            }
            bounds.y -= bounds.height / 2f;
            bounds.x -= bounds.width / 2f;
        } catch (Exception e) {
            // ????
            e.printStackTrace();
            bounds.width = 0;
            bounds.height = 0;
        }
    }

    public Unlockable getFuzzle() {
        return appearance[Appearance.FUZZLE];
    }

    public Unlockable getFrame() {
        return appearance[Appearance.FRAME];
    }

    public Unlockable getEyes() {
        return appearance[Appearance.EYES];
    }

    public Unlockable getFace() {
        return appearance[Appearance.FACE];
    }

    public Unlockable getAppearance(int index) {
        return appearance[index];
    }

    public void setAppearance(int index, Unlockable unlockable) {
        Unlockable fuzzUnlockable = appearance[index];
        if (fuzzUnlockable == unlockable && fuzzUnlockable.getColor() == unlockable.getColor()) {
            return; // No need to redraw and recalculate when fuzzle component is already drawn
        }
        appearance[index] = unlockable;
        if (ui != null && unlockable != null) {
            TextureRegionDrawable drawable = drawables[index];

            drawable.setRegion(colorizer.getColored(ui.getTextures(), unlockable, false));
            if (index == Appearance.FUZZLE) {
                sizeChanged();
            } else if (index != Appearance.FRAME) {
                updateBounds(appBounds[index], colorizer.definitionFor(unlockable), drawable);
            }
        }
    }

    public TextureRegionDrawable getFuzzleDrawable() {
        TextureRegionDrawable fuzzleDrawable = drawables[Appearance.FUZZLE];
        if (fuzzleDrawable.getRegion() == null)
            return null;
        return fuzzleDrawable;
    }

}
