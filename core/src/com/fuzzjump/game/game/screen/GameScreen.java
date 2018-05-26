package com.fuzzjump.game.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.fuzzjump.api.profile.IProfileService;
import com.fuzzjump.game.game.Assets;
import com.fuzzjump.game.game.FuzzContext;
import com.fuzzjump.game.game.map.GameMap;
import com.fuzzjump.game.game.map.builder.GameMapBuilder;
import com.fuzzjump.game.game.player.Profile;
import com.fuzzjump.game.game.player.unlockable.UnlockableColorizer;
import com.fuzzjump.game.game.screen.component.Fuzzle;
import com.fuzzjump.game.game.screen.game.GameStage;
import com.fuzzjump.game.game.screen.game.World;
import com.fuzzjump.game.game.screen.game.actors.GamePlatform;
import com.fuzzjump.game.game.screen.game.actors.GamePlayer;
import com.fuzzjump.game.game.screen.ui.GameUI;
import com.fuzzjump.game.game.screen.core.ProfileFetcher;
import com.fuzzjump.game.net.GameSession;
import com.fuzzjump.game.net.GameSessionWatcher;
import com.fuzzjump.game.util.GraphicsScheduler;
import com.fuzzjump.libgdxscreens.screen.StageScreen;
import com.fuzzjump.server.common.messages.game.Game;
import com.fuzzjump.server.common.messages.join.Join;

import java.util.Random;

import javax.inject.Inject;

import io.reactivex.Observable;

public class GameScreen extends StageScreen<GameUI> implements GameSessionWatcher {

    public enum GameState {
        NOT_LOADED,
        CONNECTING,
        AUTHORIZING,
        GAME_INITIATING,
        GAME_STARTED;
    }

    private final Object updateLock = new Object();

    private final FuzzContext context;
    private final UnlockableColorizer colorizer;
    private final GraphicsScheduler scheduler;
    private final ProfileFetcher profileFetcher;

    private GameSession gameSession;

    private GameStage worldStage;
    private World world;

    private GameMap map;
    private Profile me;
    private final Random random;

    private GamePlatform[] winnerPlatforms = new GamePlatform[4];
    private IntMap<GamePlayer> players = new IntMap<>(4, 1.0f);

    private Dialog progressDialog;
    private Image image;
    private Label status;
    private String gameId;

    private GameState state = GameState.NOT_LOADED;
    private int updateCounter;

    @Inject
    public GameScreen(GameUI ui,
                      Profile profile,
                      FuzzContext context,
                      UnlockableColorizer colorizer,
                      IProfileService profileService,
                      GraphicsScheduler scheduler,
                      ProfileFetcher profileFetcher) {
        super(ui);
        this.me = profile;
        this.context = context;
        this.colorizer = colorizer;
        this.scheduler = scheduler;
        this.random = new Random(context.getGameSeed().hashCode());
        this.profileFetcher = profileFetcher;
    }

    //MEH.
    private Array<Profile> getGamePlayerProfiles() {
        Array<Profile> profiles = new Array<>(players.size);
        for(IntMap.Entry<GamePlayer> player : players) {
            profiles.add(player.value.getProfile());
        }
        return profiles;
    }

    @Override
    public void onReady() {
        this.progressDialog = ui().actor(Dialog.class, Assets.GameUI.PROGRESS_DIALOG);
        this.image = ui().actor(Image.class, Assets.GameUI.PROGRESS_IMAGE);
        this.status = ui().actor(Label.class, Assets.GameUI.PROGRESS_LABEL);
        image.setVisible(true);
        status.setVisible(true);

        status.setText("Loading map");
        progressDialog.show(getStage());
    }

    @Override
    public void onShow() {
        System.out.println("Show");
        if (context.getIp() == null) {
            screenHandler.showScreen(MenuScreen.class);
            return;
        }
        gameId = context.getGameId();
        state = GameState.CONNECTING;
        status.setText("Connecting");

        gameSession = new GameSession(context.getIp(), context.getPort(), this);

        //send join -> send join game -> get back game ready -> send loaded -> wait for countdown.
        gameSession.getPacketProcessor()
                .addListener(Join.JoinResponsePacket.class, this::joinResponse)
                .addListener(Game.JoinGameResponse.class, this::joinGameResponse)
                .addListener(Game.GameReady.class, this::gameReady)
                .addListener(Game.Countdown.class, this::countdown);

        gameSession.connect();

        // We can do this here since GameSessionWatcher#onConnect is called on the next frame (the earliest, which is unlikely)
        world = new World(this, context.getGameSeed(), map.getWidth(), map.getHeight());

        worldStage = new GameStage(getStage().getViewport(), getStage().getBatch(), this, world);
        worldStage.init();

        new GameMapBuilder(ui().getMapTextures(), ui(), world, world.getRandom(), 1, map).build();

        worldStage.addGameActors(world.getPhysicsActors());

        addPlayer(me, world.getWidth() / 2);
    }


    public void joinResponse(GameSession session, Join.JoinResponsePacket message) {
        System.out.println("Join response");
        status.setText("Joining game");
        gameSession.send(Game.JoinGame.newBuilder().setGameId(gameId).buildPartial());
    }

    private void joinGameResponse(GameSession gameSession, Game.JoinGameResponse message) {
        if (!message.getFound()) {
            this.gameSession.disconnected();
            screenHandler.showScreen(MenuScreen.class);
        }
        state = GameState.GAME_INITIATING;
        System.out.println("Join game response");
        status.setText("Waiting for players");
    }

    private void countdown(GameSession session, Game.Countdown message) {
        status.setText("Starting: " + message.getTime());
        if (message.getTime() <= 0) {
            progressDialog.hide();
            state = GameState.GAME_STARTED;
        }
    }

    private void gameReady(GameSession session, Game.GameReady message) {
        if (message.getPlayersCount() == 0) {
            return;
        }
        System.out.println("Game ready.");
        if (message.getSendLoaded()) {
            updateCounter++;
        }
        final int thisUpdate = updateCounter;
        System.out.println("Doing it.");
        for (int i = 0; i < message.getPlayersCount(); i++) {
            Game.Player player = message.getPlayers(i);
            if (player.getUserId().equals(me.getUserId())) {
                me.setPlayerIndex(player.getPlayerIndex());
                continue;
            }
            GamePlayer existingPlayer = players.get(player.getPlayerIndex());
            Profile profile;
            if (existingPlayer == null || !existingPlayer.getProfile().getUserId().equals(player.getUserId())) {
                profile = new Profile();
                profile.setUserId(player.getUserId());
                profile.setDisplayName("Waiting");
                profile.setPlayerIndex(player.getPlayerIndex());
                addPlayer(profile, world.getWidth() / 2);
            } else {
                existingPlayer.getProfile().setPlayerIndex(player.getPlayerIndex());
            }
        }
        //mark this update counter. if another player joins while we're loading this batch of players, defer the loaded message.
        synchronized (updateLock) {
            loadProfiles().observeOn(scheduler).subscribe(result -> {
                synchronized (updateLock) {
                    if (message.getSendLoaded() && updateCounter == thisUpdate) {
                        sendLoaded();
                    }
                }
            });
        }
    }

    //wait for countdown after this is sent.
    private void sendLoaded() {
        gameSession.send(Game.Loaded.newBuilder().buildPartial());
    }

    public Observable loadProfiles() {
        return Observable.just(true);
    }

    public void playerFinished(GamePlayer player) {
        int place = 0;
        for (int i = 0; i < players.size; i++) {
            if (players.get(i).isFinished()) {
                place++;
            }
        }
        player.setFinished(true);
        GamePlatform winningPlatform = winnerPlatforms[place];
        player.setVisible(true);
        player.position.set(winningPlatform.hitbox.getX() + winningPlatform.hitbox.getWidth() / 2 - player.getWidth() / 2, winningPlatform.hitbox.getY() + winningPlatform.hitbox.getHeight() + player.getHeight() / 2);
        player.velocity.set(0, 0);
        player.velocityModifier.x = 0;
    }


    @Override
    public void render(float delta) {
        getScreenLoader().process();

        final Stage stage = getStage();

        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!stage.getBatch().isBlendingEnabled()) {
            stage.getBatch().enableBlending();
        }

        if (state == GameState.GAME_STARTED) {
            GamePlayer player = getPlayer();

            if (player.getStun() <= 0 && !player.isFinished()) {
                if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                    player.velocity.x = -10f * 200;
                } else  if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                    player.velocity.x = 10f * 200;
                } else {
                    player.velocity.x = -Gdx.input.getAccelerometerX() * 200;
                }
            }

            //Label heightLabel = ui().actor(Assets.GameUI.HEIGHT_LABEL);
            //heightLabel.setText(String.format("%06d", (int) player.getY() / 5));

            world.act(worldStage);
        }

        if (worldStage != null) {
            worldStage.draw();
        }

        stage.act(delta);
        stage.draw();

        //if (gameStarted)
        //    gameSession.sendPosition(player.position.x, player.position.y, player.velocity.x, player.velocity.y);

        if (worldStage != null && getMap().snowing()) {
            ui().getSnowActor().setVelocity(getPlayer().velocity.y);
        }

        if (Assets.DEBUG) {
            stage.getBatch().begin();
            Assets.DEBUG_FONT.draw(stage.getBatch(), "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 50);
            stage.getBatch().end();
        }
    }

    public void addPlayer(Profile profile, float x) {
        Fuzzle playerFuzzle = new Fuzzle(ui(), colorizer, profile, false).load(getScreenLoader());
        GamePlayer plr = new GamePlayer(ui(), world, playerFuzzle, profile, x, 96, 128, ui().getGameSkin().getFont("ingame-font"));
        players.put(profile.getPlayerIndex(), plr);
        worldStage.addGameActor(plr);
        world.getPhysicsActors().add(plr);
    }

    public void removePlayers() {
        for (int i = 0, n = players.size; i < n; i++) {
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
        state = GameState.AUTHORIZING;
        authorize();
    }

    private void authorize() {
        System.out.println("Authorizing");
        gameSession.send(Join.JoinPacket.newBuilder()
                .setUserId(me.getUserId())
                .setServerSessionKey(context.getSessionKey())
                .setVersion(1)
                .buildPartial());
    }

    @Override
    public void onDisconnect() {
        ui().disposeMaps();

        screenHandler.showScreen(MenuScreen.class);
    }

    public void setMap(GameMap map) {
        this.map = map;
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

    public IntMap<GamePlayer> getPlayers() {
        return players;
    }

    public GamePlayer getPlayer() {
        return players.get(me.getPlayerIndex());
    }

}
