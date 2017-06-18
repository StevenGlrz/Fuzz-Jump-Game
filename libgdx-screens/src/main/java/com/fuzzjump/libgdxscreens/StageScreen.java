package com.fuzzjump.libgdxscreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;

public abstract class StageScreen<TUI extends StageUI> extends ScreenAdapter {

    protected ScreenHandler screenHandler;

    private final StageUI ui;
    private ScreenLoader loader;

    private Stage stage;
    private boolean cleared;

    private boolean rendered;

    public StageScreen(TUI ui) {
        this.ui = ui;
        this.loader = new ScreenLoader();
    }

    //init -> add -> showScreen.
    public final void init(Stage stage, ScreenHandler handler) {
        this.stage = stage;
        this.screenHandler = handler;
        if (this.ui != null) {
            this.ui.stageScreen = this;

            try {
                ui.init();
            } catch (Exception e) {
                System.err.println("Error initializing UI");
                e.printStackTrace();
            }
        }
    }

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

        onPreRender(delta);
        stage.act(delta);
        stage.draw();
        onPostRender(delta);

        if (!rendered) {
            rendered = true;
            rendered();
        }
        cleared = false;
    }

    @Override
    public void resume() {
        super.resume();
    }

    //called after the screen has been drawn once
    public void rendered() {

    }

    public TUI getUI() {
        return (TUI) ui;
    }

    public Stage getStage() {
        return stage;
    }

    /**
     * Called once the UI has been initialized and the screen is not
     * being initalized as a cached screen.
     */
    public abstract void onReady();

    /**
     * Called once the screen is being displayed regardless of its cached state.
     */
    public abstract void showing();

    /**
     * Click management for actors on the screen
     * @param id The id of the actor.
     * @param actor The clicked actor.
     */
    public abstract void clicked(int id, Actor actor);

    public void onPreRender(float delta) {

    }

    public void onPostRender(float delta) {
        loader.process();
    }

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

    /**
     * The purpose of this function is to switch the loader to the loader of the
     * next screen. This is so our current display can load the next screen's information.
     * @param loader The screen loader.
     */
    public void setLoader(ScreenLoader loader) {
        this.loader = loader;
    }

    public ScreenLoader getLoader() {
        return loader;
    }
}
