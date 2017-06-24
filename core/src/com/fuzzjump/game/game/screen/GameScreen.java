package com.fuzzjump.game.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.fuzzjump.game.game.Assets;
import com.fuzzjump.game.game.FuzzContext;
import com.fuzzjump.game.game.map.GameMap;
import com.fuzzjump.game.game.map.GameMapGround;
import com.fuzzjump.game.game.map.GameMapParser;
import com.fuzzjump.game.game.player.Profile;
import com.fuzzjump.game.game.player.unlockable.UnlockableColorizer;
import com.fuzzjump.game.game.screen.component.Fuzzle;
import com.fuzzjump.game.game.screen.game.GameStage;
import com.fuzzjump.game.game.screen.game.World;
import com.fuzzjump.game.game.screen.game.actors.GamePlatform;
import com.fuzzjump.game.game.screen.game.actors.GamePlayer;
import com.fuzzjump.game.game.screen.ui.GameUI;
import com.fuzzjump.game.net.GameSession;
import com.fuzzjump.game.net.GameSessionWatcher;
import com.fuzzjump.libgdxscreens.Textures;
import com.fuzzjump.libgdxscreens.screen.StageScreen;
import com.fuzzjump.server.common.messages.game.Game;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.inject.Inject;

public class GameScreen extends StageScreen<GameUI> implements GameSessionWatcher {

    private GameSession session;
    private final FuzzContext context;
    private final GameMapParser mapParser;
    private final UnlockableColorizer colorizer;

    private long totalTime = 0, lastTime = 0;
    private int totalPackets = 0;

    private GameStage worldStage;
    private World world;

    private TextureAtlas mapTextures;
    private GameMap map;
    private Profile me;
    private Random random;

    private GamePlatform[] winnerPlatforms = new GamePlatform[4];
    private Map<Integer, GamePlayer> players = new HashMap<>(4);

    private String mapName;
    private boolean gameStarted;
    private long currentTime;
    private float gameTime = 0;
    private DelayAction delayAction;

    private Dialog progressDialog;
    private Image image;
    private Label status;
    private String gameId;

    @Inject
    public GameScreen(GameUI ui, Profile profile, FuzzContext context, GameMapParser mapParser, UnlockableColorizer colorizer) {
        super(ui);
        this.me = profile;
        this.context = context;
        this.mapParser = mapParser;
        this.colorizer = colorizer;
    }

    @Override
    public void onReady() {
        this.progressDialog = ui().actor(Dialog.class, Assets.GameUI.PROGRESS_DIALOG);
        this.image = ui().actor(Image.class, Assets.GameUI.PROGRESS_IMAGE);
        this.status = ui().actor(Label.class, Assets.GameUI.PROGRESS_LABEL);
        image.setVisible(true);
        status.setVisible(true);
        status.setText("Loading map");
        //progressDialog.show(game.getStage());

        Gdx.app.postRunnable(this::initScreen);
    }

    private void initScreen() {
        gameId = context.getGameId();
        random = new Random(context.getGameSeed());
        mapName = GameMap.MAPS[context.getGameMap()];

        map = mapParser.parse(mapName);
        mapTextures = Textures.atlasFromFolder(Assets.MAP_DIR + mapName + "/");
        world = new World(this, context.getGameSeed(), map.getWidth(), map.getHeight());

        GameMapGround ground = map.getGround();
        GamePlatform platform = new GamePlatform(world, 0, ground.getY(), world.getWidth(), ground.getHeight(), ground.getHeight() * .85f, ground.getRealHeight(), mapTextures.findRegion(ground.getName())) {

            @Override
            public void draw(Batch batch, float parentAlpha) {
                batch.setColor(Color.WHITE);
                super.draw(batch, parentAlpha);
            }
        };
        world.getPhysicsActors().add(platform);

        this.worldStage = new GameStage(getStage().getViewport(), getStage().getBatch(), this);
        worldStage.init();
        worldStage.addGameActors(world.getPhysicsActors());

        addPlayer(me, world.getWidth() / 2);

        status.setText("Connecting");
        /*if (attachment.getIp() != null) {
            session = new GameSession(attachment.getIp(), attachment.getPort(), attachment.getKey(), this);
            session.connect();

            // TODO Add handlers
        } else {
            gameStarted = true;
            progressDialog.hide();
        }*/
    }

    private void loadedResponse(GameSession gameSession, Game.LoadedResponse message) {
        if (message.getFound()) {
            status.setText("Waiting for players");
        } else {
            session.disconnected();
            screenHandler.showScreen(MenuScreen.class);
        }
    }

 //   private void countdown(GameSession session, Game.Countdown countdown) {
//        status.setText("Starting: " + countdown.getTime());
//        if (countdown.getTime() <= 0) {
//            progressDialog.hide();
//            gameStarted = true;
//        }
 //  }

  //  private void gameReady(GameSession session, Game.GameReady message) {
//        if (message.getPlayersCount() > 0) {
//            PlayerProfile[] newPlayers = new PlayerProfile[message.getPlayersCount() - 1];
//            System.out.println("PLAYERS: " + message.getPlayersCount());
//            int arrIndex = 0;
//            for (int i = 0; i < message.getPlayersCount(); i++) {
//                Game.Player player = message.getPlayers(i);
//                if (player.getProfileId() == game.getProfile().getProfileId()) {
//                    game.getProfile().setPlayerIndex(player.getPlayerIndex());
//                    continue;
//                }
//                GamePlayer existingPlayer = players.get(player.getPlayerIndex());
//                PlayerProfile profile;
//                if (existingPlayer == null || existingPlayer.getProfile().getProfileId() != player.getProfileId()) {
//                    profile = new PlayerProfile(player);
//                    profile.setName("Waiting");
//                } else {
//                    profile = existingPlayer.getProfile();
//                }
//                newPlayers[arrIndex++] = profile;
//            }
//            removePlayers();
//            addPlayer(game.getProfile(), world.getWidth() / 2);
//            for (int i = 0; i < newPlayers.length; i++) {
//                PlayerProfile profile = newPlayers[i];
//                addPlayer(profile, world.getWidth() / 2);
//            }
//        }
  //  }

    public Profile getProfile(long profileId) {
        for (int i = 0; i < players.size(); i++) {
            Profile profile = players.get(i).getProfile();
            if (profile.getProfileId() == profileId) {
                return profile;
            }
        }
        return null;
    }

    public void playerFinished(GamePlayer player) {
        int place = 0;
        for (int i = 0; i < getPlayers().size(); i++) {
            if (getPlayers().get(i).isFinished()) {
                place++;
            }
        }
        player.setFinished(true);
        GamePlatform winningPlatform = winnerPlatforms[place];
        player.position.set(winningPlatform.hitbox.getX() + winningPlatform.hitbox.getWidth() / 2 - player.getWidth() / 2, winningPlatform.hitbox.getY() + winningPlatform.hitbox.getHeight() + player.getHeight() / 2);
        player.velocity.set(0, 0);
        player.velocityModifier.x = 0;
    }

    @Override
    public void onShow() {
        //if (!session.established())
        //    onDisconnect(); // TODO This should NOT be done here
    }

    @Override
    public void render(float delta) {
        final Stage stage = getStage();

        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!stage.getBatch().isBlendingEnabled()) {
            stage.getBatch().enableBlending();
        }

        gameTime += delta;

        if (gameStarted) {
            GamePlayer player = getPlayer();

            if (player.getStun() <= 0 && !player.isFinished()) {
                player.velocity.x = -Gdx.input.getAccelerometerX() * 200;
            }

            //Label heightLabel = ui().actor(Assets.GameUI.HEIGHT_LABEL);
            //heightLabel.setText(String.format("%06d", (int) player.getY() / 5));

            world.act(worldStage);
        }

        if (worldStage != null)
            worldStage.draw();

        stage.act(delta);
        stage.draw();

        //if (gameStarted)
        //    session.sendPosition(player.position.x, player.position.y, player.velocity.x, player.velocity.y);

//        if (worldStage != null && getMap().snowing())
//            game.getSnow().draw(game.getBatch(), 1, getPlayer().velocity.y);

        if (Assets.DEBUG) {
            stage.getBatch().begin();
            Assets.DEBUG_FONT.draw(stage.getBatch(), "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 50);
            stage.getBatch().end();
        }

    }

    public GamePlayer addPlayer(Profile profile, float x) {
        Fuzzle playerFuzzle = new Fuzzle(ui(), colorizer, profile, false);
        playerFuzzle.load(getLoader());
        GamePlayer plr = new GamePlayer(ui(), world, playerFuzzle, profile, x, 96, 128, ui().getGameSkin().getFont("ingame-font"));
        players.put(profile.getPlayerIndex(), plr);
        worldStage.addGameActor(plr);
        world.getPhysicsActors().add(plr);
        return plr;
    }

    public void removePlayers() {
        for (int i = 0, n = players.size(); i < n; i++) {
            GamePlayer player = players.get(i);
            worldStage.removeGameActor(player);
            world.getPhysicsActors().remove(player);
        }
        players.clear();
    }

    @Override
    public void clicked(int id, Actor actor) {

    }

    @Override
    public void onConnect() {
        status.setText("Authorizing");
    }

    @Override
    public void onDisconnect() {
        mapTextures.dispose();
        screenHandler.showScreen(MenuScreen.class);
    }

    @Override
    public void onTimeout() {
        onDisconnect();
    }

    public void authenticated() {
        status.setText("Joining game");
        session.send(Game.Loaded.newBuilder().setGameId(gameId).buildPartial());
    }

    public TextureAtlas getMapTextures() {
        return mapTextures;
    }

    public GameMap getMap() {
        return map;
    }

    public Random getRandom() {
        return random;
    }

    public World getWorld() {
        return world;
    }

    public Map<Integer, GamePlayer> getPlayers() {
        return players;
    }

    public GamePlayer getPlayer() {
        return players.get(me.getPlayerIndex());
    }

}
