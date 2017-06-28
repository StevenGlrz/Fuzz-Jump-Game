package com.fuzzjump.game.game.screen.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fuzzjump.game.game.map.GameMapGround;
import com.fuzzjump.game.game.screen.GameScreen;
import com.fuzzjump.game.game.screen.game.actors.CloudActor;
import com.fuzzjump.game.game.screen.game.actors.GamePlatform;
import com.fuzzjump.game.game.screen.game.actors.GamePlayer;
import com.fuzzjump.game.game.map.GameMap;
import com.fuzzjump.game.game.map.GameMapBackground;

import java.util.List;
import java.util.Random;

public class GameStage extends Stage {

    private final Viewport mainViewport;
    private final Batch batch;
    private final GameScreen screen;
    private final Random random;
    private final GameMap map;
    private final TextureAtlas textures;
    private final World world;

    private TextureRegion borderTexture;

    private ParallaxCamera gameCamera;

    private Group gameActors = new Group();
    private Group clouds = new Group();

    public GameStage(Viewport viewport, Batch batch, GameScreen screen, World world) {
        super(new ScreenViewport(), batch);
        this.mainViewport = viewport;
        this.batch = batch;
        this.screen = screen;
        this.textures = screen.getMapTextures();
        this.map = screen.getMap();
        this.random = screen.getRandom();
        this.world = world;
    }

    public void init() {

        for (GameMapBackground background : map.getBackgrounds()) {
            background.setTexture(textures.findRegion(background.getName()));
        }

        gameCamera = new ParallaxCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        getViewport().setCamera(gameCamera);
        ((ScreenViewport) getViewport()).setUnitsPerPixel(screen.getWorld().getWidth() / Gdx.graphics.getWidth());

        getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        mainViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        borderTexture = textures.findRegion("border");

        gameActors = new Group();
        clouds = new Group();

        if (map.isCloudEnabled())
            createClouds();

        addActor(gameActors);

        TextureRegion winnerRegion = screen.ui().getTextures().getTexture("winners-platform");
        Image winnersPlatform = new Image(winnerRegion);
        winnersPlatform.setSize(winnerRegion.getRegionWidth(), winnerRegion.getRegionHeight());

        winnersPlatform.setX(getViewport().getWorldWidth() / 2 - winnersPlatform.getWidth() / 2);
        winnersPlatform.setY(screen.getWorld().getHeight() + GameMap.PLATFORM_GAP / 4);

        addGameActor(winnersPlatform);

        GameMapGround ground = map.getGround();
        GamePlatform platform = new GamePlatform(world, 0, ground.getY(), world.getWidth(), ground.getHeight(), ground.getHeight() * .85f, ground.getRealHeight(), screen.getMapTextures().findRegion(ground.getName())) {

            @Override
            public void draw(Batch batch, float parentAlpha) {
                batch.setColor(Color.WHITE);
                super.draw(batch, parentAlpha);
            }
        };
        world.getPhysicsActors().add(platform);
    }

    @Override
    public void draw() {
        gameCamera.update();

        List<GameMapBackground> background = map.getBackgrounds();

        batch.begin();
        for (int i = 0, n = background.size(); i < n; i++) {
            GameMapBackground bg = map.getBackgrounds().get(i);

            float width = bg.getBounds().x == -1 ? map.getWidth() : bg.getBounds().x;
            float height = bg.getBounds().y == -1 ? map.getHeight() : bg.getBounds().y;

            batch.setProjectionMatrix(gameCamera.calculateParallaxMatrix(bg.getParallax().x, bg.getParallax().y));
            float y =  (-getViewport().getWorldHeight() / 2f) + (getViewport().getWorldHeight() * bg.getYOffset()) + bg.getLayerOffset();
            batch.draw(bg.getTexture(), 0, y, width, height);
        }
        //batch.end();
        batch.setProjectionMatrix(gameCamera.combined);
        //batch.begin();
        gameActors.draw(batch, 1);
        drawBorders();
        batch.end();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        GamePlayer player = screen.getPlayer();
        if (player.getX() - getViewport().getWorldWidth() / 2 > 0 && player.getX() + getViewport().getWorldWidth() / 2 < screen.getWorld().getWidth())
            gameCamera.position.x = player.getX();
        gameCamera.position.y = player.getY();
    }

    private void createClouds() {
        int worldHeight = map.getHeight() - 1280;
        int blockSize = worldHeight / 60;
        for (int block = 0; block < worldHeight / blockSize; block++) {
            int platformsPerBlock = 1 + random.nextInt(2);
            for (int i = 0; i < platformsPerBlock; i++) {
                int y = 1280 + (block * blockSize + random.nextInt(blockSize));
                int x = random.nextInt(map.getWidth());
                CloudActor cloud = new CloudActor(screen);
                cloud.setPosition(x, y);
                clouds.addActor(cloud);
            }
        }
        addGameActor(clouds);
    }

    public void addGameActors(List<? extends Actor> actors) {
        for (int i = 0, n = actors.size(); i < n; i++)
            gameActors.addActor(actors.get(i));
    }

    public void addGameActor(Actor actor) {
        gameActors.addActor(actor);
    }

    public void removeGameActor(Actor actor) {
        gameActors.removeActor(actor);
    }

    private void drawBorders() {
        GamePlayer player = screen.getPlayer();
        float playerY = 0;
        if (player != null) {
            playerY = player.getY();
        }
        if (playerY - getViewport().getWorldHeight() / 2 < map.getGround().getY()) {
            //determine how many blocks to draw
            int xBlocks = (int) (1 + getViewport().getWorldWidth() / borderTexture.getRegionWidth()); //+ 1 to be gratuitous
            int yBlocks = (int) (Math.abs(playerY - getViewport().getWorldHeight() / 2) + map.getGround().getY()) / borderTexture.getRegionHeight();

            for (int x = 0; x < xBlocks + 2; x++) {
                for (int y = 1; y < yBlocks + 2; y++) {
                    int realX = (x * borderTexture.getRegionWidth());
                    int realY = (int) (map.getGround().getY() - (y * borderTexture.getRegionHeight()));
                    batch.draw(borderTexture, realX, realY);
                }
            }
        }
    }


    public GameScreen getScreen() {
        return screen;
    }

}