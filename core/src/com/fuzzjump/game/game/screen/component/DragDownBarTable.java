package com.fuzzjump.game.game.screen.component;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;

public class DragDownBarTable extends Table {

    protected Table titleBarTable;
    protected Table dragDownTable;
    protected Table contentTable;

    private float dragTriggerPercent;
    private float dragPanelHeight;

    private boolean tapDown;
    private boolean autoDragging;
    private boolean dragDownDisabled;


    /**
     * Initializes a new instance of a DragDownBarTable
     * @param titleBarTable The table for the title bar
     * @param dragDownTable The table that is dragged down
     * @param contentTable The table that the content is shown on
     * @param dragDownDisabled Is the drag down panel enabled?
     * @param tapDown If tapping the titlebar will bring the dragdown table down. If dragDownDisabled is true then this variable is useless
     * @param dragTriggerPercent How far in percent it takes to drag until it automatically opens if you let go. Relative to the whole tables size(titleBartable + contentTable height)
     */
    public DragDownBarTable(Table titleBarTable, Table dragDownTable, Table contentTable, boolean dragDownDisabled, boolean tapDown, float dragTriggerPercent, float dragPanelHeight) {
        this.dragDownDisabled = dragDownDisabled;
        this.tapDown = tapDown;
        this.titleBarTable = titleBarTable == null ? new Table() : titleBarTable;
        this.dragDownTable = dragDownTable == null ? new Table() : dragDownTable;
        this.contentTable = contentTable == null ? new Table() : contentTable;
        this.dragTriggerPercent = dragTriggerPercent;
        this.dragPanelHeight = dragPanelHeight;
        this.titleBarTable.setTouchable(Touchable.enabled);
        init();
    }

    public DragDownBarTable(Table titleBarTable, Table dragDownTable, Table contentTable, boolean dragDownDisabled) {
        this(titleBarTable, dragDownTable, contentTable, dragDownDisabled, true, .25f, .50f);
    }

    protected void init() {
        add(titleBarTable).prefWidth(Value.percentWidth(1f, this)).prefHeight(Value.percentWidth(.154f, this));
        row();
        add(contentTable).prefWidth(Value.percentWidth(1f, this)).prefHeight(Value.percentHeight(1, this)).expand();
        addActor(dragDownTable);
        pack();
        dragDownTable.setY(getHeight());
        titleBarTable.addListener(new DragListener() {

            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {

            }

            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                if (dragDownDisabled)
                    return;
                if (autoDragging)
                    return;
                dragDown(event.getStageY());
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer) {
                if (dragDownDisabled)
                    return;
                if (!isDown() && !isUp() && !autoDragging) {
                    autoDrag(false);
                }
            }

        });
        if (tapDown) {
            titleBarTable.addListener(new ClickListener() {

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (dragDownDisabled)
                        return;
                    //if we take this out it will always collapse when you let it go
                    if (autoDragging)
                        return;
                    autoDrag(true);
                }

            });
        }

        titleBarTable.setZIndex(2);
        dragDownTable.setZIndex(1);
        contentTable.setZIndex(0);
    }

    @Override
    public void sizeChanged() {
        super.sizeChanged();
        dragDownTable.setHeight(getHeight() * dragPanelHeight);
        dragDownTable.setWidth(getWidth());
        dragDownTable.setY(getHeight());
    }

    public void dragDown(float position) {
        position += titleBarTable.getHeight() / 2f;
        position = Math.min(position, getHeight());
        position = Math.max(position, getHeight() - dragDownTable.getHeight());
        dragDownTable.setY(position);
        titleBarTable.setY(position - titleBarTable.getHeight());
    }

    public void autoDrag(boolean tapped) {
        autoDragging = true;
        float targetY;
        float dragDownTablePosition = 1f - (dragDownTable.getY() / getHeight());
        if ((tapped && isUp()) || (!tapped && dragDownTablePosition > dragTriggerPercent)) {
            targetY = getHeight() - dragDownTable.getHeight();
        } else {
            targetY = getHeight();
        }
        dragDownTable.addAction(Actions.moveTo(dragDownTable.getX(), targetY, .15f));
        titleBarTable.addAction(Actions.sequence(Actions.moveTo(titleBarTable.getX(), targetY - titleBarTable.getHeight(), .15f), Actions.run(new Runnable() {
            @Override
            public void run() {
                autoDragging = false;
            }
        })));
    }

    public boolean isDown() {
        return getHeight() - dragDownTable.getY() == dragDownTable.getHeight();
    }

    public boolean isUp() {
        return dragDownTable.getY() == getHeight();
    }

    public Table getContentTable() {
        return contentTable;
    }

    public Table getDragDownTable() {
        return dragDownTable;
    }

    public Table getTitleBarTable() {
        return titleBarTable;
    }

}

