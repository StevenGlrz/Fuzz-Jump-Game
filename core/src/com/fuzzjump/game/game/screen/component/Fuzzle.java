package com.fuzzjump.game.game.screen.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.fuzzjump.game.game.player.Appearance;
import com.fuzzjump.game.game.player.Profile;
import com.fuzzjump.game.game.player.unlockable.Unlockable;
import com.fuzzjump.game.game.player.unlockable.UnlockableColorizer;
import com.fuzzjump.game.game.player.unlockable.UnlockableDefinition;
import com.fuzzjump.libgdxscreens.StageUI;

/**
 * Created by stephen on 8/21/2015.
 */
public class Fuzzle extends Actor implements Appearance.AppearanceChangeListener {

    private final UnlockableColorizer colorizer;

    private TextureRegionDrawable questionMark;
    private Profile profile;
    private StageUI ui;

    private Rectangle fuzzleBounds;
    private Rectangle headBounds;
    private Rectangle eyesBounds;
    private Rectangle faceBounds;

    private Unlockable fuzzle;
    private Unlockable frame;
    private Unlockable head;
    private Unlockable eyes;
    private Unlockable face;

    private TextureRegionDrawable fuzzleDrawable;
    private TextureRegionDrawable frameDrawable;
    private TextureRegionDrawable headDrawable;
    private TextureRegionDrawable eyesDrawable;
    private TextureRegionDrawable faceDrawable;

    private boolean drawFrame = true;

    public Fuzzle(StageUI ui, UnlockableColorizer colorizer) {
        super();
        this.ui = ui;
        this.colorizer = colorizer;

        this.fuzzleBounds = new Rectangle();
        this.headBounds = new Rectangle();
        this.eyesBounds = new Rectangle();
        this.faceBounds = new Rectangle();
        this.questionMark = ui.getTextures().getTextureRegionDrawable("ui-question-mark");
        this.fuzzleDrawable = new TextureRegionDrawable();
        this.frameDrawable = new TextureRegionDrawable();
        this.headDrawable = new TextureRegionDrawable();
        this.eyesDrawable = new TextureRegionDrawable();
        this.faceDrawable = new TextureRegionDrawable();
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
        if (profile == this.profile)
            return;
        if (this.profile != null)
            this.profile.getAppearance().removeChangeListener(this);
        this.profile = profile;
        profile.getAppearance().addChangeListener(this);
        appearanceChanged();
    }

    @Override
    public void appearanceChanged() {
        setFuzzle(profile.getAppearance().getEquip(Appearance.FUZZLE));
        setFrame(profile.getAppearance().getEquip(Appearance.FRAME));
        setEyes(profile.getAppearance().getEquip(Appearance.EYES));
        setFace(profile.getAppearance().getEquip(Appearance.FACE));
        setHead(profile.getAppearance().getEquip(Appearance.HEAD));
    }

    @Override
    public void sizeChanged() {
        super.sizeChanged();
        update();
    }

    public void update() {
        if (fuzzle == null || fuzzleDrawable == null)
            return;
        updateFuzzleBounds();
        if (head != null)
            updateBounds(headBounds, head.getDefinition(), headDrawable);
        if (eyes != null)
            updateBounds(eyesBounds, eyes.getDefinition(), eyesDrawable);
        if (face != null)
            updateBounds(faceBounds, face.getDefinition(), faceDrawable);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(Color.WHITE);
        super.draw(batch, parentAlpha);
        if (frameDrawable.getRegion() == null || fuzzleDrawable.getRegion() ==  null) {
            float questionMarkWidth = getWidth() * .85f;
            float questionMarkHeight = questionMarkWidth / (questionMark.getRegion().getRegionWidth() / questionMark.getRegion().getRegionHeight());
            questionMark.draw(batch, getX() + getWidth() / 2 - questionMarkWidth / 2, getY() + getHeight() / 2 - questionMarkHeight / 2, questionMarkWidth, questionMarkHeight);
            return;
        }
        if (drawFrame)
            frameDrawable.draw(batch, getX(), getY(), getWidth(), getHeight());
        fuzzleDrawable.draw(batch, getX() + fuzzleBounds.x, getY() + fuzzleBounds.y, fuzzleBounds.width, fuzzleBounds.height);
        drawPart(batch, face, faceDrawable, faceBounds);
        drawPart(batch, eyes, eyesDrawable, eyesBounds);
        drawPart(batch, head, headDrawable, headBounds);
    }

    public void drawPart(Batch batch, Unlockable unlockable, TextureRegionDrawable drawable, Rectangle bounds) {
        if (unlockable == null || bounds.width == 0 || bounds.height == 0)
            return;
        drawable.draw(batch, getX() + bounds.x, getY() + bounds.y, bounds.width, bounds.height);
    }

    public void updateFuzzleBounds() {
        float fuzzleWidth = drawFrame ? getWidth() * .85f : getWidth();
        float fuzzleHeight = fuzzleWidth / (fuzzleDrawable.getRegion().getRegionWidth() / fuzzleDrawable.getRegion().getRegionHeight());
        fuzzleBounds.x = getWidth() / 2 - fuzzleWidth / 2;
        fuzzleBounds.y = getHeight() / 2 - fuzzleHeight / 2;
        fuzzleBounds.width = fuzzleWidth;
        fuzzleBounds.height = fuzzleHeight;
    }

    public void updateBounds(Rectangle bounds, UnlockableDefinition definition, TextureRegionDrawable drawable) {
        try {
            if (definition.getBounds() == null)
                return;
            Rectangle uBound = definition.getBounds()[fuzzle.getDefinition().getId()];
            bounds.x = fuzzleBounds.x + (uBound.x * fuzzleBounds.width);
            bounds.y = fuzzleBounds.y + (uBound.y * fuzzleBounds.height);
            if (uBound.height == -1) {
                bounds.width = fuzzleBounds.width * uBound.width;
                bounds.height = bounds.width / ((float)drawable.getRegion().getRegionWidth() / drawable.getRegion().getRegionHeight());
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
            e.printStackTrace();
            bounds.width = 0;
            bounds.height = 0;
        }
    }

    public Unlockable getFuzzle() {
        return fuzzle;
    }

    public void setFuzzle(Unlockable fuzzle) {
        this.fuzzle = fuzzle;
        if (fuzzle != null) {
            fuzzleDrawable.setRegion(colorizer.getColored(ui.getTextures(), fuzzle, false));
            sizeChanged();
        }
    }

    public Unlockable getFrame() {
        return frame;
    }

    public void setFrame(Unlockable frame) {
        this.frame = frame;
        if (ui != null && frame != null)
            frameDrawable.setRegion(colorizer.getColored(ui.getTextures(), frame, false));
    }

    public Unlockable getHead() {
        return head;
    }

    public void setHead(Unlockable head) {
        this.head = head;
        update(head, headBounds, headDrawable);
    }

    public Unlockable getEyes() {
        return eyes;
    }

    public void setEyes(Unlockable eyes) {
        this.eyes = eyes;
        update(eyes, eyesBounds, eyesDrawable);
    }

    public Unlockable getFace() {
        return face;
    }

    public void setFace(Unlockable face) {
        this.face = face;
        update(face, faceBounds, faceDrawable);
    }

    private void update(Unlockable unlockable, Rectangle bounds, TextureRegionDrawable drawable) {
        if (ui != null && unlockable != null) {
            drawable.setRegion(colorizer.getColored(ui.getTextures(), unlockable, false));
            updateBounds(bounds, unlockable.getDefinition(), drawable);
        }
    }

    public TextureRegionDrawable getFuzzleDrawable() {
        if (fuzzleDrawable.getRegion() == null)
            return null;
        return fuzzleDrawable;
    }

    public TextureRegionDrawable getFrameDrawable() {
        if (frameDrawable.getRegion() == null)
            return null;
        return frameDrawable;
    }

    public TextureRegionDrawable getHeadDrawable() {
        if (headDrawable.getRegion() == null)
            return null;
        return headDrawable;
    }

    public TextureRegionDrawable getEyesDrawable() {
        if (eyesDrawable.getRegion() == null)
            return null;
        return eyesDrawable;
    }

    public TextureRegionDrawable getFaceDrawable() {
        if (faceDrawable.getRegion() == null)
            return null;
        return faceDrawable;
    }

    @Override
    public boolean remove() {
        boolean rem = super.remove();
        if (rem) {
            profile.getAppearance().removeChangeListener(this);
        }
        return rem;
    }

}
