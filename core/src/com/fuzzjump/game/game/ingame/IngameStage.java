package com.fuzzjump.game.game.ingame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.fuzzjump.game.FuzzJump;
import com.fuzzjump.game.game.screens.GameScreen;
import com.fuzzjump.game.game.ingame.actors.CloudActor;
import com.fuzzjump.game.game.ingame.actors.Player;
import com.fuzzjump.game.model.map.GameMap;
import com.fuzzjump.game.model.map.GameMapBackground;

import java.util.List;
import java.util.Random;

public class IngameStage extends Stage {

    private final FuzzJump game;
    private final GameScreen screen;
    private final Random random;
    private final GameMap map;
    private final TextureAtlas textures;

    private TextureRegion borderTexture;

    private ParallaxCamera gameCamera;

    private Group gameActors = new Group();
    private Group clouds = new Group();

    public IngameStage(FuzzJump game, GameScreen screen) {
        super(new ScreenViewport(), game.getBatch());
        this.game = game;
        this.screen = screen;
        this.textures = screen.getTextures();
        this.map = screen.getMap();
        this.random = screen.getRandom();
    }

    public void init() {

        for (GameMapBackground background : map.getBackgrounds()) {
            background.setTexture(textures.findRegion(background.getName()));
        }

        gameCamera = new ParallaxCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        getViewport().setCamera(gameCamera);
        ((ScreenViewport) getViewport()).setUnitsPerPixel(screen.getWorld().getWidth() / Gdx.graphics.getWidth());

        getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        game.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        borderTexture = textures.findRegion("border");

        gameActors = new Group();
        clouds = new Group();

        if (map.isCloudEnabled())
            createClouds();

        addActor(gameActors);

        TextureRegion winnerRegion = screen.ui().getTexture("winners-platform");
        Image winnersPlatform = new Image(winnerRegion);
        winnersPlatform.setSize(winnerRegion.getRegionWidth(), winnerRegion.getRegionHeight());

        winnersPlatform.setX(getViewport().getWorldWidth() / 2 - winnersPlatform.getWidth() / 2);
        winnersPlatform.setY(screen.getWorld().getHeight() + GameMap.PLATFORM_GAP / 4);

        addGameActor(winnersPlatform);

    }

    @Override
    public void draw() {
        gameCamera.update();

        List<GameMapBackground> background = map.getBackgrounds();

        game.getBatch().begin();
        for (int i = 0, n = background.size(); i < n; i++) {
            GameMapBackground bg = map.getBackgrounds().get(i);

            float width = bg.getBounds().x == -1 ? map.getWidth() : bg.getBounds().x;
            float height = bg.getBounds().y == -1 ? map.getHeight() : bg.getBounds().y;

            game.getBatch().setProjectionMatrix(gameCamera.calculateParallaxMatrix(bg.getParallax().x, bg.getParallax().y));
            float y =  (-getViewport().getWorldHeight() / 2f) + (getViewport().getWorldHeight() * bg.getYOffset()) + bg.getLayerOffset();
            game.getBatch().draw(bg.getTexture(), 0, y, width, height);
        }
        //game.getBatch().end();
        game.getBatch().setProjectionMatrix(gameCamera.combined);
        //game.getBatch().begin();
        gameActors.draw(game.getBatch(), 1);
        drawBorders();
        game.getBatch().end();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        Player player = screen.getPlayer();
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
        Player player = screen.getPlayer();

        if (player.getY() - getViewport().getWorldHeight() / 2 < map.getGround().getY()) {
            //determine how many blocks to draw
            int xBlocks = (int) (1 + getViewport().getWorldWidth() / borderTexture.getRegionWidth()); //+ 1 to be gratuitous
            int yBlocks = (int) (Math.abs(player.getY() - getViewport().getWorldHeight() / 2) + map.getGround().getY()) / borderTexture.getRegionHeight();

            for (int x = 0; x < xBlocks + 2; x++) {
                for (int y = 1; y < yBlocks + 2; y++) {
                    int realX = (x * borderTexture.getRegionWidth());
                    int realY = (int) (map.getGround().getY() - (y * borderTexture.getRegionHeight()));
                    game.getBatch().draw(borderTexture, realX, realY);
                }
            }
        }
    }


    public GameScreen getScreen() {
        return screen;
    }

}