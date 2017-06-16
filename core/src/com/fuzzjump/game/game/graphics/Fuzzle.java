package com.fuzzjump.game.game.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.fuzzjump.game.game.player.Appearance;
import com.fuzzjump.game.game.player.Profile;
import com.fuzzjump.game.game.player.unlockable.Unlockable;
import com.fuzzjump.game.game.player.unlockable.UnlockableBound;
import com.fuzzjump.game.game.player.unlockable.UnlockableDefinition;
import com.fuzzjump.game.game.player.unlockable.UnlockableDefinitions;
import com.fuzzjump.libgdxscreens.StageUI;

/**
 * Created by stephen on 8/21/2015.
 */
public class Fuzzle extends Actor implements Appearance.AppearanceChangeListener {

    private final UnlockableDefinitions definitions;

    private Unlockable defaultFuzzleUnlockable;
    private Unlockable defaultFrameUnlockable;

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

    public Fuzzle(StageUI ui, UnlockableDefinitions definitions) {
        super();
        this.ui = ui;
        this.definitions = definitions;

        this.defaultFuzzleUnlockable = new Unlockable(definitions.getDefinition(0), 0, -1);;
        this.defaultFrameUnlockable = new Unlockable(definitions.getDefinition(32), 1, -1);

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

    public Fuzzle(StageUI ui, UnlockableDefinitions definitions, Profile profile) {
        this(ui, definitions);
        setProfile(profile);
    }

    public Fuzzle(StageUI ui, UnlockableDefinitions definitions, Profile profile, boolean drawFrame) {
        this(ui, definitions, profile);
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
        setFuzzle(profile.getAppearance().getEquip(Appearance.Equipment.FUZZLE));
        setFrame(profile.getAppearance().getEquip(Appearance.Equipment.FRAME));
        setEyes(profile.getAppearance().getEquip(Appearance.Equipment.EYES));
        setFace(profile.getAppearance().getEquip(Appearance.Equipment.FACE));
        setHead(profile.getAppearance().getEquip(Appearance.Equipment.HEAD));
    }

    @Override
    public void sizeChanged() {
        super.sizeChanged();
        System.out.println("sizedchanged");
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
            UnlockableBound uBound = definition.getBounds()[fuzzle.getDefinition().getId()];
            bounds.x = (float) (fuzzleBounds.x + (uBound.x * fuzzleBounds.width));
            bounds.y = (float) (fuzzleBounds.y + (uBound.y * fuzzleBounds.height));
            if (uBound.h == -1) {
                bounds.width = (float) (fuzzleBounds.width * uBound.w);
                bounds.height = bounds.width / ((float)drawable.getRegion().getRegionWidth() / drawable.getRegion().getRegionHeight());
            } else if (uBound.w == -1) {
                bounds.height = (float) (fuzzleBounds.height * uBound.h);
                bounds.width = bounds.height * ((float) drawable.getRegion().getRegionWidth() / drawable.getRegion().getRegionHeight());
            } else {
                bounds.width = (float) (fuzzleBounds.width * uBound.w);
                bounds.height = (float) (fuzzleBounds.height * uBound.h);
            }
            bounds.y -= bounds.height / 2f;
            bounds.x -= bounds.width / 2f;
            System.out.println(bounds.toString());
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
        if (fuzzle == null) {
            fuzzle = defaultFuzzleUnlockable;
        }
        this.fuzzle = fuzzle;
        if (fuzzle != null) {
            fuzzleDrawable.setRegion(definitions.getColored(ui.getTextures(), fuzzle, false));
            sizeChanged();
        }
    }

    public Unlockable getFrame() {
        return frame;
    }

    public void setFrame(Unlockable frame) {
        if (frame == null) {
            frame = defaultFrameUnlockable;
        }
        this.frame = frame;
        if (ui != null && frame != null)
            frameDrawable.setRegion(definitions.getColored(ui.getTextures(), frame, false));
    }

    public Unlockable getHead() {
        return head;
    }

    public void setHead(Unlockable head) {
        this.head = head;
        if (ui != null && head != null) {
            headDrawable.setRegion(definitions.getColored(ui.getTextures(), head, false));
            updateBounds(headBounds, head.getDefinition(), headDrawable);
        }
    }

    public Unlockable getEyes() {
        return eyes;
    }

    public void setEyes(Unlockable eyes) {
        this.eyes = eyes;
        if (ui != null && eyes != null) {
            eyesDrawable.setRegion(definitions.getColored(ui.getTextures(), eyes, false));
            updateBounds(eyesBounds, eyes.getDefinition(), eyesDrawable);
        }
    }

    public Unlockable getFace() {
        return face;
    }

    public void setFace(Unlockable face) {
        this.face = face;
        if (ui != null && face != null) {
            faceDrawable.setRegion(definitions.getColored(ui.getTextures(), face, false));
            updateBounds(faceBounds, face.getDefinition(), faceDrawable);
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
