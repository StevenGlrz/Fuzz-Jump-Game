package com.fuzzjump.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fuzzjump.game.game.ScreenHandler;
import com.fuzzjump.game.game.Textures;
import com.fuzzjump.game.game.UnlockableColorizer;
import com.fuzzjump.game.game.ingame.actors.SnowActor;
import com.fuzzjump.game.game.screens.GameScreen;
import com.fuzzjump.game.game.screens.LoginScreen;
import com.fuzzjump.game.game.screens.MenuScreen;
import com.fuzzjump.game.game.screens.SplashScreen;
import com.fuzzjump.game.game.screens.WaitingScreen;
import com.fuzzjump.game.model.character.UnlockableDefinitions;
import com.fuzzjump.game.model.map.GameMapParser;
import com.fuzzjump.game.model.profile.LocalProfile;

import org.jrenner.smartfont.SmartFontGenerator;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FuzzJump extends Game {

    public static final boolean DEBUG = true;
    public static BitmapFont DEBUG_FONT = null;

    public static FuzzJump Game;

    private final VectorGraphicsLoader vectorGraphicsLoader;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Skin skin;
    private Textures textures;
    private Stage uiStage;
    private UnlockableDefinitions unlockableDefinitions;

    private final GameMapParser mapParser = new GameMapParser();

    private LocalProfile profile;
    private SnowActor snow;
    private ScreenHandler screenHandler;
    private boolean christmasTime;

    private UnlockableColorizer colorizer;
    private ExecutorService workerService;

    public FuzzJump(ExecutorService workerService, VectorGraphicsLoader vectorGraphicsLoader) {
        this.workerService = workerService;
        this.vectorGraphicsLoader = vectorGraphicsLoader;
    }

    @Override
    public void create() {
        FuzzJump.Game = this;

        skin = new Skin();

        textures = new Textures(new UnlockableColorizer());

        christmasTime = Calendar.getInstance().get(Calendar.MONTH) == Calendar.DECEMBER;

        batch = new SpriteBatch();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport = new ScreenViewport(camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        camera.update();

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        uiStage = new Stage(getViewport(), getBatch());

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(getStage());

        Gdx.input.setInputProcessor(multiplexer);

        Gdx.gl.glClearColor(1, 1, 1, 1);
		/*
		 * Load UI skin
		 */
        skin.addRegions(new TextureAtlas("main.pack"));

        skin.add("ui_background", new TextureRegion(new Texture(Gdx.files.internal("data/maps/sunny_day_map/bg-sky.png"))));
        if (isChristmasTime()) {
            skin.add("ui_ground", new TextureRegion(new Texture(Gdx.files.internal("data/maps/snow_map/bg-snow-ground.png"))));
        } else {
            skin.add("ui_ground", new TextureRegion(new Texture(Gdx.files.internal("data/maps/sunny_day_map/ground.png"))));
        }

        /*
         * Load screens
         */
        screenHandler = new ScreenHandler(this, textures);
        screenHandler.addScreen(SplashScreen.class, 0);
        screenHandler.addScreen(LoginScreen.class, 0);
        screenHandler.addScreen(MenuScreen.class, 2);
        screenHandler.addScreen(WaitingScreen.class, 0);
        screenHandler.addScreen(GameScreen.class, 0);

        screenHandler.showScreen(SplashScreen.class, null);
    }

    public void init(){
        textures.load();
        snow = new SnowActor(textures, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Color shadow = new Color(0, 0, 0, .4f);

		/*
		 * Load fonts
		 */
        SmartFontGenerator smartGen = new SmartFontGenerator();
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("Grandstander-clean.ttf"));
        FreeTypeFontParameter param = new FreeTypeFontParameter();
        param.genMipMaps = true;

        param.size = Gdx.graphics.getHeight() / 10;
        param.shadowColor = shadow;
        param.shadowOffsetY = Gdx.graphics.getHeight() / 375;
        skin.add("large-font", smartGen.createFont(gen, "large-font", param), BitmapFont.class);

        param.size = Gdx.graphics.getHeight() / 20;
        skin.add("big-font", smartGen.createFont(gen, "big-font", param), BitmapFont.class);

        param.size = Gdx.graphics.getHeight() / 30;
        skin.add("default-font", smartGen.createFont(gen, "default-font", param), BitmapFont.class);

        param.size = Gdx.graphics.getHeight() / 45;
        skin.add("profile-font", smartGen.createFont(gen, "profile-font", param), BitmapFont.class);
        DEBUG_FONT = skin.getFont("profile-font");

        param.size = Gdx.graphics.getHeight() / 60;
        skin.add("ingame-font", smartGen.createFont(gen, "ingame-font", param), BitmapFont.class);

        param.size = Gdx.graphics.getHeight() / 70;
        skin.add("small-font", smartGen.createFont(gen, "small-font", param), BitmapFont.class);

        param.size = 25;
        skin.add("ingame-small-font", smartGen.createFont(gen, "ingame-small-font", param), BitmapFont.class);

        gen.dispose();

        skin.load(Gdx.files.internal("uiskin.json"));

        unlockableDefinitions = new UnlockableDefinitions();
        unlockableDefinitions.init();

        profile = new LocalProfile();

        //TODO load data.
//        if (preferences.containsKey("data")) {
//            try {
//                profile.load(new JSONObject(preferences.getString("data")));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
    }

    public void onPause() {
        System.out.println("Paused?");
        //if (GameSession.getCurrentSession() != null) {
        //    // Stop the game if the app has been paused. TODO Maybe a timer?
        //    GameSession.destroySession();
        //}
    }

    @Override
    public void setScreen(Screen screen) {
        super.setScreen(screen);
    }


    public SpriteBatch getBatch() {
        return batch;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public Skin getSkin() {
        return skin;
    }

    public Stage getStage() {
        return uiStage;
    }

    public LocalProfile getProfile() {
        return profile;
    }

    public GameMapParser getMapParser() {
        return mapParser;
    }

    public boolean isChristmasTime() {
        return christmasTime;
    }

    public SnowActor getSnow() {
        return snow;
    }

    public boolean playingSound() {
        //return preferences.containsKey("sound") && preferences.getString("sound").equals("1"); TODO playing sound
        return false;
    }

    public void toggleSound() {
        //preferences.put("sound", playingSound() ? "0" : "1"); TODO preferences stuff
    }

    public UnlockableDefinitions getUnlockableDefinitions() {
        return unlockableDefinitions;
    }

    public ExecutorService getWorkerService() {
        return workerService;
    }

    public void loginWithFacebook() {
        // TODO - Filler
    }
}
