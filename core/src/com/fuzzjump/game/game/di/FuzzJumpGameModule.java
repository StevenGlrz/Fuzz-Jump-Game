package com.fuzzjump.game.game.di;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.fuzzjump.game.FuzzJump;
import com.fuzzjump.game.game.FuzzContext;
import com.fuzzjump.game.game.map.GameMapParser;
import com.fuzzjump.game.game.player.unlockable.UnlockableColorizer;
import com.fuzzjump.game.game.player.unlockable.UnlockableRepository;
import com.fuzzjump.libgdxscreens.screen.ScreenHandler;
import com.fuzzjump.libgdxscreens.screen.ScreenResolver;
import com.fuzzjump.libgdxscreens.Textures;
import com.fuzzjump.libgdxscreens.VectorGraphicsLoader;

import dagger.Module;
import dagger.Provides;

@Module
public class FuzzJumpGameModule {

    @FuzzJumpScope
    @Provides
    public ScreenResolver provideScreenResolver(FuzzJump fuzzJump) {
        return new FuzzJumpScreenResolver(fuzzJump);
    }

    @FuzzJumpScope
    @Provides
    public UnlockableRepository provideUnlockableDefinitions() {
        return new UnlockableRepository();
    }

    @FuzzJumpScope
    @Provides
    public FuzzContext provideFuzzContext() {
        return new FuzzContext();
    }

    @FuzzJumpScope
    @Provides
    public GameMapParser provideGameMapParser() {
        return new GameMapParser();
    }

    @FuzzJumpScope
    @Provides
    public UnlockableColorizer provideUnlockableColorizer(VectorGraphicsLoader graphicsLoader) {
        return new UnlockableColorizer(graphicsLoader);
    }

    @FuzzJumpScope
    @Provides
    public Skin provideSkin() {
        return new Skin();
    }

    @FuzzJumpScope
    @Provides
    public Textures provideTextures(VectorGraphicsLoader graphicsLoader) {
        return new Textures(graphicsLoader);
    }

    @FuzzJumpScope
    @Provides
    public SpriteBatch provideSpriteBatch() {
        return new SpriteBatch();
    }

    @FuzzJumpScope
    @Provides
    public OrthographicCamera provideOrthographicCamera() {
        return new OrthographicCamera();
    }

    @FuzzJumpScope
    @Provides
    public ScreenViewport provideScreenViewport(OrthographicCamera camera) {
        return new ScreenViewport(camera);
    }

    @FuzzJumpScope
    @Provides
    public Stage provideStage(ScreenViewport viewport, SpriteBatch batch) {
        return new Stage(viewport, batch);
    }

    @FuzzJumpScope
    @Provides
    public ScreenHandler provideScreenHandler(Stage stage, Textures textures, ScreenResolver screenResolver) {
        return new ScreenHandler(stage, textures, screenResolver);
    }

}
