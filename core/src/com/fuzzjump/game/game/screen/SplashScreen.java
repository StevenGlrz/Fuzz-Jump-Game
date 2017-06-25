package com.fuzzjump.game.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.fuzzjump.api.user.model.RegisterResponse;
import com.fuzzjump.game.game.Assets;
import com.fuzzjump.game.game.player.Profile;
import com.fuzzjump.game.game.player.unlockable.UnlockableColorizer;
import com.fuzzjump.game.game.player.unlockable.UnlockableRepository;
import com.fuzzjump.game.game.screen.ui.SplashUI;
import com.fuzzjump.api.TokenInterceptor;
import com.fuzzjump.libgdxscreens.Textures;
import com.fuzzjump.libgdxscreens.VectorGraphicsLoader;
import com.fuzzjump.libgdxscreens.screen.ScreenLoader;
import com.fuzzjump.libgdxscreens.screen.StageScreen;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jrenner.smartfont.SmartFontGenerator;

import javax.inject.Inject;

/**
 * Created by Steven Galarza on 6/15/2017.
 */
public class SplashScreen extends StageScreen<SplashUI> {

    private final Textures textures;
    private final Skin skin;
    private final Profile profile;
    private final UnlockableRepository definitions;
    private final UnlockableColorizer colorizer;
    private final Preferences preferences;
    private final Gson gson;
    private final TokenInterceptor interceptor;

    @Inject
    public SplashScreen(SplashUI ui, Textures textures, Skin skin, Profile profile, UnlockableRepository definitions, UnlockableColorizer colorizer, Preferences preferences, Gson gson, TokenInterceptor interceptor) {
        super(ui);
        this.textures = textures;
        this.skin = skin;
        this.profile = profile;
        this.definitions = definitions;
        this.colorizer = colorizer;
        this.preferences = preferences;
        this.gson = gson;
        this.interceptor = interceptor;
    }

    @Override
    public void onReady() {
        ScreenLoader loader = getLoader();
        Color shadow = new Color(0, 0, 0, .4f);
        SmartFontGenerator smartGen = new SmartFontGenerator();
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("Grandstander-clean.ttf"));
        FreeTypeFontParameter param = new FreeTypeFontParameter();

        param.genMipMaps = true;
        param.shadowColor = shadow;
        param.shadowOffsetY = Gdx.graphics.getHeight() / 375;

        textures.add(new VectorGraphicsLoader.VectorDetail(Assets.LOGO, Assets.UI_LOGO, "screen_width:.8", "screen_width:.6"));

        skin.addRegions(new TextureAtlas("main.pack"));
        skin.add(Assets.UI_BACKGROUND, new TextureRegion(new Texture(Gdx.files.internal(Assets.SUNNY_DAY_SKY))));
        skin.add(Assets.UI_GROUND, new TextureRegion(new Texture(Gdx.files.internal(Assets.SUNNY_DAY_GROUND))));

        int screenHeight = Gdx.graphics.getHeight();

        // Load fonts.
        loader.add(() -> createFont(Assets.LARGE_FONT, smartGen, gen, param, screenHeight / 10));
        loader.add(() -> createFont(Assets.BIG_FONT, smartGen, gen, param, screenHeight / 20));
        loader.add(() -> createFont(Assets.DEFAULT_FONT, smartGen, gen, param, screenHeight / 30));
        loader.add(() -> {
            createFont(Assets.PROFILE_FONT, smartGen, gen, param, screenHeight / 45);
            Assets.DEBUG_FONT = skin.getFont(Assets.PROFILE_FONT);
        });
        loader.add(() -> createFont(Assets.INGAME_FONT, smartGen, gen, param, screenHeight / 60));
        loader.add(() -> createFont(Assets.SMALL_FONT, smartGen, gen, param, screenHeight / 70));
        loader.add(() -> createFont(Assets.SMALL_INGAME_FONT, smartGen, gen, param, 25));

        // Load skin
        loader.add(() -> skin.load(Gdx.files.internal(Assets.SKIN)));

        // Load textures
        loader.add(this::loadTextures);

        // Load unlockable definitions
        loader.add(definitions::init);

        // Preload Fuzzles
        for (int i = 0; i < Assets.FUZZLE_COUNT; i++) {
            final int index = i;
            loader.add(() -> colorizer.getColored(ui().getTextures(), definitions.getDefinition(index), 0, false));
        }

        // Preload frames and help compiler optimize SVG loading code
        for (int i = 32, n = i + 2; i < n; i++) {
            // TODO Not necessary if Fuzzles haven't been cached, but that is only the case on first run, but we want to prevent this if fuzzles are cached in order to have a shorter first time start up
            final int index = i;
            loader.add(() -> colorizer.getColored(ui().getTextures(), definitions.getDefinition(index), 0, false));
        }

        // Once we're done ...
        loader.onDone(this::onLoadDone);

        ui().drawSplash();
    }

    @Override
    public void onShow() {

    }

    @Override
    public void clicked(int id, Actor actor) {

    }

    private void onLoadDone() {
        String profileData = preferences.getString(Assets.PROFILE_DATA, null);
        String userToken = preferences.getString(Assets.USER_TOKEN, null);
        if (profileData != null && profileData.length() > 0) {
            profile.load(gson.fromJson(profileData, RegisterResponse.RegisterBody.class));
            if (userToken == null) {
                throw new IllegalStateException("Found user data but token wasn't available - this shouldn't happen.");
            }
            interceptor.setToken(userToken);
            screenHandler.showScreen(MenuScreen.class);
        } else {
            screenHandler.showScreen(MainScreen.class);
        }
    }

    private void loadTextures() {
        FileHandle svgs = Gdx.files.internal(Assets.SVG_RESOURCE);
        VectorGraphicsLoader.VectorDetail[] details = gson.fromJson(svgs.readString(), VectorGraphicsLoader.VectorDetail[].class);
        for (int i = 0, n = details.length; i < n; i++) {
            textures.add(details[i]);
        }
    }

    private void createFont(String name, SmartFontGenerator smartGen, FreeTypeFontGenerator gen, FreeTypeFontParameter param, int size) {
        param.size = size;
        skin.add(name, gen.generateFont(param), BitmapFont.class);
    }
}
