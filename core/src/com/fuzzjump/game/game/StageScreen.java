package com.fuzzjump.game.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.fuzzjump.game.FuzzJump;
import com.fuzzjump.game.game.screens.attachment.ScreenAttachment;

public abstract class StageScreen<T extends ScreenAttachment> extends ScreenAdapter {

    private final Textures textures;

    protected FuzzJump game;
    protected ScreenHandler screenHandler;
    private StageUI ui;

    public StageScreen(FuzzJump game, Textures textures, ScreenHandler handler) {
        this.game = game;
        this.textures = textures;
        this.screenHandler = handler;
    }

    //init -> load -> showScreen.
    public final void init() {
        this.ui = createUI();
        this.ui.stageScreen = this;
        this.ui.game = game;
        this.ui.textures = textures;

        game.getStage().addActor(ui);

        try {
            ui.init();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error initializing ui");
        }
    }

    @Override
    public void show() {
        showScreen();
    }


    private int drawn = 0;

    @Override
    public void render(float delta) {
        if (drawn == 1) {
            rendered();
            drawn = 2;
        }
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0, 0, 0, 1);

        game.getStage().act(delta);
        game.getStage().draw();

        if (game.isChristmasTime() && game.getSnow() != null)
            game.getSnow().draw(game.getBatch(), 1, 0);
        if (drawn == 0)
            drawn = 1;
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void dispose() {
    }

    //called after the screen has been drawn once
    public void rendered() {

    }

    public StageUI ui() {
        return ui;
    }

    public abstract void load(T attachment);

    public abstract void showScreen();

    public abstract StageUI createUI();

    public abstract void clicked(int id, Actor actor);

    public FuzzJump getGame() {
        return game;
    }

    public void backPressed() {
        if (ui != null)
            ui.backPressed();
    }

    public void closeKeyboard() {
        game.getStage().setKeyboardFocus(null);
        Gdx.input.setOnscreenKeyboardVisible(false);
    }

    @Override
    public void hide() {
        ui.remove();
        //remove dialogs
        for (Actor actor : ui().actors()) {
            if (actor instanceof Dialog) {
                ((Dialog) actor).hide();
            }
        }
    }

    public void initCache() {
        game.getStage().addActor(ui);
        ui.invalidateHierarchy();
    }
}
