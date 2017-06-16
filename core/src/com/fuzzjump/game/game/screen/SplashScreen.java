package com.fuzzjump.game.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.fuzzjump.game.game.screen.ui.SplashUI;
import com.fuzzjump.game.service.user.IUserService;
import com.fuzzjump.libgdxscreens.StageScreen;
import com.fuzzjump.libgdxscreens.Textures;
import com.fuzzjump.libgdxscreens.VectorGraphicsLoader;

import javax.inject.Inject;

/**
 * Created by steve on 6/15/2017.
 */
public class SplashScreen extends StageScreen<SplashUI> {

    private final IUserService userService;
    private final Textures textures;
    private final Skin skin;

    @Inject
    public SplashScreen(SplashUI ui, IUserService userService, Textures textures, Skin skin) {
        super(ui);
        this.userService = userService;
        this.textures = textures;
        this.skin = skin;
    }

    @Override
    public void initialize() {
        textures.add(new VectorGraphicsLoader.VectorDetail("data/svg/logo.svg", "logo", "screen_width:.8", "screen_width:.6"));

        skin.addRegions(new TextureAtlas("main.pack"));

        skin.add("ui_background", new TextureRegion(new Texture(Gdx.files.internal("data/maps/sunny_day_map/bg-sky.png"))));
        skin.add("ui_ground", new TextureRegion(new Texture(Gdx.files.internal("data/maps/sunny_day_map/ground.png"))));

        getUI().drawSplash();
    }

    @Override
    public void showing() {

    }
}
