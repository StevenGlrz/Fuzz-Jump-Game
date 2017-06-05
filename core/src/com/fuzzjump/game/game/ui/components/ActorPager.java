package com.fuzzjump.game.game.ui.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Kerpow Games, LLC
 * Created by stephen on 4/10/2015.
 */
public class ActorPager extends Table {

    private String[] titles;
    private Actor[] components;
    private Button leftBtn;
    private Button rightBtn;
    private Label titleLbl;
    private Drawable lock;
    private LockingDecision lockDecision;
    private IndexChangeListener indexChangeListener;
    private float speed = .25f;

    private int index = 0;

    public ClickListener listener = new ClickListener() {

        public void clicked(InputEvent e, float x, float y) {
            float moveToX = 0; //offscreen coordinate for the current actor to move to
            float nextX = 0; //next actor's offscreen x coordinate
            final Actor current = components[index];
            int oldIndex = index;
            if (e.getListenerActor() == leftBtn) {
                moveToX = current.getWidth() * 1.5f;
                nextX = -current.getWidth() * 1.5f;
                index--;
            } else if (e.getListenerActor() == rightBtn) {
                moveToX = -getWidth() * 1.5f;
                nextX = current.getWidth() * 1.5f;
                index++;
            }
            if (index < 0) index = components.length - 1;
            index %= components.length;
            Actor next = components[index];
            addActor(next);
            next.setBounds(nextX, current.getY(), current.getWidth(), current.getHeight());
            next.addAction(Actions.moveTo(getWidth() / 2 - next.getWidth() / 2, next.getY(), speed));
            if (titles != null)
                titleLbl.setText(titles[index]);
            if (indexChangeListener != null)
                indexChangeListener.indexChanged(oldIndex, index);
            current.addAction(Actions.sequence(Actions.moveTo(moveToX, current.getY(), speed), Actions.run(new Runnable() {
                @Override
                public void run() {
                    removeActor(current);
                }
            })));
        }
    };

    public ActorPager(String[] titles, Actor[] components, Label titleLbl, Button leftBtn, Button rightBtn) {
        this.titles = titles;
        this.components = components;
        this.leftBtn = leftBtn;
        this.rightBtn = rightBtn;
        this.titleLbl = titleLbl;
    }

    public ActorPager(String[] titles, Actor[] components, Label titleLbl, Button leftBtn, Button rightBtn, int startIdx) {
        this(titles, components, titleLbl, leftBtn, rightBtn);
        index = startIdx;
    }

    //I wish function pointers were a thing in java
    public void setLockingEnabled(Drawable lock, LockingDecision decision) {
        this.lock = lock;
        this.lockDecision = decision;
    }

    public void setListener(IndexChangeListener listener) {
        this.indexChangeListener = listener;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void addActors() {
        leftBtn.addListener(listener);
        rightBtn.addListener(listener);

        if (titles != null) {
            add(titleLbl).colspan(3).expand();
            row();
        }
        defaults().padBottom(Value.percentHeight(.05f, this)).padTop(Value.percentHeight(.05f, this));
        add(leftBtn).left().size(Value.percentWidth(.15f, this));
        add(components[index]).center().height(Value.percentHeight(.55f, this)).width(Value.percentWidth(.60f, this));
        add(rightBtn).right().size(Value.percentWidth(.15f, this));

        leftBtn.setZIndex(1000);
        rightBtn.setZIndex(999);

        setClip(true);
    }

    @Override
    public void draw(Batch batch, float alpha) {
        super.draw(batch, alpha);
        if (lock != null) {
            applyTransform(batch, computeTransform());
            if (lockDecision.isLocked(index)) {
                float padLeft = this.getPadLeftValue().get(this), padBottom = this.getPadBottomValue().get(this);
                if (clipBegin(padLeft, padBottom, getWidth() - padLeft - getPadRightValue().get(this),
                        getHeight() - padBottom - getPadTopValue().get(this))) {
                    float currentX = components[index].getX();
                    float currentY = components[index].getY();
                    float currentWidth = components[index].getWidth();
                    float currentHeight = components[index].getHeight();
                    float lockWidth = (getHeight() / 2f) / 1.666f;
                    float lockHeight = getHeight() / 2f;
                    lock.draw(batch, currentX + (currentWidth / 2) - lockWidth / 2, currentY + (currentHeight / 2) - lockHeight / 2, lockWidth, lockHeight);
                    batch.flush();
                    clipEnd();
                }
            }
            resetTransform(batch);
        }
    }

    public interface LockingDecision {

        boolean isLocked(int index);

    }

    public interface IndexChangeListener {

        void indexChanged(int oldIndex, int newIndex);

    }
}
