package com.fuzzjump.libgdxscreens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;

public abstract class StageScreen<TUI extends StageUI> extends ScreenAdapter {

    protected ScreenHandler screenHandler;

    private StageUI ui;

    private Stage stage;
    private boolean cleared;

    public StageScreen(TUI ui) {
        this.ui = ui;
    }

    //init -> add -> showScreen.
    public final void init(Stage stage, ScreenHandler handler) {
        this.stage = stage;
        this.screenHandler = handler;
        this.ui = ui;
        if (this.ui != null) {
            this.ui.stageScreen = this;

            try {
                ui.init();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error initializing getUI");
            }
        }
    }

    private int drawn = 0;

    protected final void clear() {
        cleared = true;
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    protected final void showDialog(Dialog dialog, Stage stage) {
        dialog.show(stage, null);
        dialog.setPosition(Math.round((stage.getWidth() - dialog.getWidth()) / 2), Math.round((stage.getHeight() - dialog.getHeight()) / 2));
    }

    @Override
    public void render(float delta) {
        if (!cleared) {
            clear();
        }
        if (drawn == 1) {
            rendered();
            drawn = 2;
        }
        stage.act(delta);

        stage.draw();

        if (drawn == 0)
            drawn++;
        cleared = false;
    }

    @Override
    public void resume() {
        super.resume();
    }

    //called after the screen has been drawn once
    public void rendered() {

    }

    public StageUI getUI() {
        return ui;
    }

    public Stage getStage() {
        return stage;
    }

    public abstract void initialize();

    public abstract void showing();

    public void backPressed() {
        if (ui != null)
            ui.backPressed();
    }

    @Override
    public void hide() {
        ui.remove();
        //remove dialogs
        for (Actor actor : ui.getActors()) {
            if (actor instanceof Dialog) {
                ((Dialog) actor).hide();
            }
        }
    }

    public void initCache() {
        if (ui != null) {
            stage.addActor(ui);
            ui.invalidateHierarchy();
        }
    }
}
