package com.fuzzjump.game.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.fuzzjump.game.FuzzJump;
import com.fuzzjump.game.game.screen.GameScreen;
import com.fuzzjump.game.game.screen.MainScreen;
import com.fuzzjump.game.game.screen.MenuScreen;
import com.fuzzjump.game.game.screen.SplashScreen;
import com.fuzzjump.game.game.screen.WaitingScreen;
import com.fuzzjump.libgdxscreens.screen.ScreenHandler;
import com.fuzzjump.libgdxscreens.Textures;

import javax.inject.Inject;

public class FuzzJumpGame implements ScreenHandler.ScreenChangeHandler {

    private final ScreenHandler screenHandler;
    private final SpriteBatch batch;
    private final Stage stage;
    private final Textures textures;

    //I really dont like this.
    //No one likes you ^
    //fuck off
    private FuzzJump fuzzJump;

    @Inject
    public FuzzJumpGame(FuzzJump fuzzJump,
                        ScreenHandler screenHandler,
                        SpriteBatch batch,
                        Stage stage,
                        Textures textures) {
        this.fuzzJump = fuzzJump;
        this.screenHandler = screenHandler;
        this.batch = batch;
        this.stage = stage;
        this.textures = textures;
        this.screenHandler.setScreenChangeHandler(this);
    }

    public void create() {
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Configure input processor
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);

        //  Clear graphics
        Gdx.gl.glClearColor(1, 1, 1, 1);

        // Configure screens
        screenHandler.addScreen(SplashScreen.class, 0);
        screenHandler.addScreen(MainScreen.class, 0);
        screenHandler.addScreen(MenuScreen.class, 2);
        screenHandler.addScreen(WaitingScreen.class, 0);
        screenHandler.addScreen(GameScreen.class, 0);

        screenHandler.showScreen(SplashScreen.class);
    }

    @Override
    public void changeScreen(Screen screen) {
        fuzzJump.setScreen(screen);
    }

}
