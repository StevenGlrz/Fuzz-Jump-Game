package com.fuzzjump.game.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.fuzzjump.game.FuzzJump;
import com.fuzzjump.game.game.ScreenHandler;
import com.fuzzjump.game.game.StageIds;
import com.fuzzjump.game.game.StageScreen;
import com.fuzzjump.game.game.StageUI;
import com.fuzzjump.game.game.Textures;
import com.fuzzjump.game.game.ingame.IngameStage;
import com.fuzzjump.game.game.ingame.actors.Platform;
import com.fuzzjump.game.game.ingame.actors.Player;
import com.fuzzjump.game.game.screens.attachment.GameScreenAttachment;
import com.fuzzjump.game.game.screens.attachment.MenuScreenAttachment;
import com.fuzzjump.game.game.ui.GameUI;
import com.fuzzjump.game.model.World;
import com.fuzzjump.game.model.map.GameMap;
import com.fuzzjump.game.model.map.GameMapGround;
import com.fuzzjump.game.model.map.builder.GameMapBuilder;
import com.fuzzjump.game.model.profile.PlayerProfile;
import com.fuzzjump.game.model.profile.Profile;
import com.fuzzjump.game.net.GameSession;
import com.fuzzjump.game.net.GameSessionWatcher;
import com.fuzzjump.game.net.requests.GetAppearanceRequest;
import com.fuzzjump.game.net.requests.WebRequest;
import com.fuzzjump.game.net.requests.WebRequestCallback;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kerpowgames.fuzzjump.common.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameScreen extends StageScreen<GameScreenAttachment> implements GameSessionWatcher {

    private GameSession session;

    private long totalTime = 0, lastTime = 0;
    private int totalPackets = 0;

    private IngameStage worldStage;
    private GameUI gameUI;
    private World world;

    private TextureAtlas textures;
    private GameMap map;
    private Profile me;
    private Random random;

    private Platform[] winnerPlatforms = new Platform[4];
    private Map<Integer, Player> players = new HashMap<>(4);

    private String mapName;
    private boolean gameStarted;
    private long currentTime;
    private float gameTime = 0;
    private DelayAction delayAction;

    private Dialog progressDialog;
    private Image image;
    private Label status;
    private String gameId;

    private WebRequestCallback getAppearanceCallback = new WebRequestCallback() {
        @Override
        public void onResponse(JsonObject response) {
            updateAppearances(response);
        }
    };

    public GameScreen(FuzzJump game, Textures textures, ScreenHandler handler) {
        super(game, textures, handler);
    }

    @Override
    public void load(final GameScreenAttachment attachment) {
        this.progressDialog = ui().actor(Dialog.class, StageIds.GameUI.PROGRESS_DIALOG);
        this.image = ui().actor(Image.class, StageIds.GameUI.PROGRESS_IMAGE);
        this.status = ui().actor(Label.class, StageIds.GameUI.PROGRESS_LABEL);
        image.setVisible(true);
        status.setVisible(true);
        status.setText("Loading map");
        progressDialog.show(game.getStage());
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                initScreen(attachment);
            }
        });

    }

    private void initScreen(GameScreenAttachment attachment) {
        gameId = attachment.getGameId();
        me = game.getProfile();
        random = new Random(attachment.getSeed());
        mapName = GameMap.MAPS[attachment.getMapId()];

        map = game.getMapParser().parse(mapName);
        textures = Textures.atlasFromFolder("data/maps/" + mapName + "/");
        world = new World(game, this, attachment.getSeed(), map.getWidth(), map.getHeight());

        GameMapGround ground = map.getGround();
        Platform platform = new Platform(world, 0, ground.getY(), world.getWidth(), ground.getHeight(), ground.getHeight() * .85f, ground.getRealHeight(), textures.findRegion(ground.getName())) {

            @Override
            public void draw(Batch batch, float parentAlpha) {
                batch.setColor(Color.WHITE);
                super.draw(batch, parentAlpha);
            }
        };
        world.getPhysicsActors().add(platform);

        GameMapBuilder bldr = new GameMapBuilder(textures, ui(), world, random, attachment.getRank(), map);
        bldr.build();

        this.worldStage = new IngameStage(game, this);
        worldStage.init();
        worldStage.addGameActors(world.getPhysicsActors());

        addPlayer(game.getProfile(), world.getWidth() / 2).getSpecials().initSpecials();
        getPlayer().getSpecials().initSpecials();

        status.setText("Connecting");
        if (attachment.getIp() != null) {
            session = new GameSession(attachment.getIp(), attachment.getPort(), attachment.getKey(), this);
            session.connect();
            session.getPacketHandler().addListener(Game.LoadedResponse.class, new PacketHandler.PacketListener<GameSession, Game.LoadedResponse>() {
                @Override
                public void received(GameSession gameSession, Game.LoadedResponse loadedResponse) {
                    loadedResponse(gameSession, loadedResponse);
                }
            });
            session.getPacketHandler().addListener(Game.Countdown.class, new PacketHandler.PacketListener<GameSession, Game.Countdown>() {
                @Override
                public void received(GameSession gameSession, Game.Countdown countdown) {
                    countdown(gameSession, countdown);
                }
            });
            session.getPacketHandler().addListener(Game.GameReady.class, new PacketHandler.PacketListener<GameSession, Game.GameReady>() {
                @Override
                public void received(GameSession gameSession, Game.GameReady gameReady) {
                    gameReady(gameSession, gameReady);
                }
            });
        } else {
            gameStarted = true;
            progressDialog.hide();
        }
    }

    private void loadedResponse(GameSession gameSession, Game.LoadedResponse message) {
        if (message.getFound()) {
            status.setText("Waiting for players");
        } else {
            session.disconnected();
            screenHandler.showScreen(MenuScreen.class, new MenuScreenAttachment(false, true));
        }
    }

    private void countdown(GameSession session, Game.Countdown countdown) {
        status.setText("Starting: " + countdown.getTime());
        if (countdown.getTime() <= 0) {
            progressDialog.hide();
            gameStarted = true;
        }
    }

    private void gameReady(GameSession session, Game.GameReady message) {
        if (message.getPlayersCount() > 0) {
            PlayerProfile[] newPlayers = new PlayerProfile[message.getPlayersCount() - 1];
            System.out.println("PLAYERS: " + message.getPlayersCount());
            int arrIndex = 0;
            for (int i = 0; i < message.getPlayersCount(); i++) {
                Game.Player player = message.getPlayers(i);
                if (player.getProfileId() == game.getProfile().getProfileId()) {
                    game.getProfile().setPlayerIndex(player.getPlayerIndex());
                    continue;
                }
                Player existingPlayer = players.get(player.getPlayerIndex());
                PlayerProfile profile;
                if (existingPlayer == null || existingPlayer.getProfile().getProfileId() != player.getProfileId()) {
                    profile = new PlayerProfile(player);
                    profile.setName("Waiting");
                } else {
                    profile = existingPlayer.getProfile();
                }
                newPlayers[arrIndex++] = profile;
            }
            removePlayers();
            addPlayer(game.getProfile(), world.getWidth() / 2);
            for (int i = 0; i < newPlayers.length; i++) {
                PlayerProfile profile = newPlayers[i];
                addPlayer(profile, world.getWidth() / 2);
            }
            downloadAppearances();
        }
    }

    public void downloadAppearances() {
        ArrayList<Long> ids = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            PlayerProfile profile = players.get(i).getProfile();
            if (profile.getAppearance().loaded())
                continue;
            ids.add(profile.getProfileId());
        }
        if (ids.size() == 0)
            return;
        long[] ihatejava = new long[ids.size()];
        for (int i = 0; i < ids.size(); i++)
            ihatejava[i] = ids.get(i);
        GetAppearanceRequest getAppearanceRequest = new GetAppearanceRequest(ihatejava, false);
        getAppearanceRequest.connect(getAppearanceCallback);
    }

    public void updateAppearances(JsonObject response) {
        synchronized (getAppearanceCallback) {
            try {
                if (response.has(WebRequest.RESPONSE_KEY) && response.get(WebRequest.RESPONSE_KEY).getAsInt() == WebRequest.SUCCESS) {
                    JsonArray payload = response.getAsJsonArray(WebRequest.PAYLOAD_KEY);
                    for (int i = 0; i < payload.size(); i++) {
                        JsonObject appearance = payload.get(i).getAsJsonObject();
                        long profileId = appearance.get("ProfileId").getAsLong();
                        PlayerProfile profile = getProfile(profileId);
                        if (profile == null)
                            continue;
                        profile.setName(appearance.get("DisplayName").getAsString());
                        profile.getAppearance().load(appearance);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                // ...
            }
        }
    }

    public PlayerProfile getProfile(long profileId) {
        for (int i = 0; i < players.size(); i++) {
            PlayerProfile profile = players.get(i).getProfile();
            if (profile.getProfileId() == profileId)
                return profile;
        }
        return null;
    }

    public void playerFinished(Player player) {
        int place = 0;
        for (int i = 0; i < getPlayers().size(); i++) {
            if (getPlayers().get(i).isFinished()) {
                place++;
            }
        }
        player.setFinished(true);
        Platform winningPlatform = winnerPlatforms[place];
        player.position.set(winningPlatform.hitbox.getX() + winningPlatform.hitbox.getWidth() / 2 - player.getWidth() / 2, winningPlatform.hitbox.getY() + winningPlatform.hitbox.getHeight() + player.getHeight() / 2);
        player.velocity.set(0, 0);
        player.velocityModifier.x = 0;
    }

    @Override
    public void showScreen() {
        //if (!session.established())
        //    onDisconnect(); // TODO This should NOT be done here
    }

    @Override
    public void render(float delta) {
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!game.getBatch().isBlendingEnabled())
            game.getBatch().enableBlending();

        gameTime += delta;

        if (gameStarted) {
            Player player = getPlayer();

            if (player.getStun() <= 0 && !player.isFinished()) {
                player.velocity.x = -Gdx.input.getAccelerometerX() * 200;
            }

            //Label heightLabel = ui().actor(StageIds.GameUI.HEIGHT_LABEL);
            //heightLabel.setText(String.format("%06d", (int) player.getY() / 5));

            world.act(worldStage);
        }

        if (worldStage != null)
            worldStage.draw();

        game.getStage().act(delta);
        game.getStage().draw();

        //if (gameStarted)
        //    session.sendPosition(player.position.x, player.position.y, player.velocity.x, player.velocity.y);

        if (worldStage != null && getMap().snowing())
            game.getSnow().draw(game.getBatch(), 1, getPlayer().velocity.y);

        if (FuzzJump.DEBUG) {
            game.getStage().getBatch().begin();
            FuzzJump.DEBUG_FONT.draw(game.getStage().getBatch(), "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 50);
            game.getStage().getBatch().end();
        }

    }

    public Player addPlayer(PlayerProfile profile, float x) {
        Player plr = new Player(ui(), world, profile, x, 96, 128, game.getSkin().getFont("ingame-font"));
        players.put(profile.getPlayerIndex(), plr);
        worldStage.addGameActor(plr);
        world.getPhysicsActors().add(plr);
        return plr;
    }

    public void removePlayers() {
        for (int i = 0, n = players.size(); i < n; i++) {
            Player player = players.get(i);
            worldStage.removeGameActor(player);
            world.getPhysicsActors().remove(player);
        }
        players.clear();
    }

    @Override
    public StageUI createUI() {
        gameUI = new GameUI(this);
        return gameUI;
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
        textures.dispose();
        getPlayer().getSpecials().dispose();
        screenHandler.showScreen(MenuScreen.class, new MenuScreenAttachment(false, true));
    }

    @Override
    public void onTimeout() {
        onDisconnect();
    }

    @Override
    public void authenticated() {
        status.setText("Joining game");
        session.send(Game.Loaded.newBuilder().setGameId(gameId).buildPartial());
    }

    //should NEVER happen
    @Override
    public void onTransferred() {
        status.setText("Transferring");
    }

    public TextureAtlas getTextures() {
        return textures;
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

    public Map<Integer, Player> getPlayers() {
        return players;
    }

    public Player getPlayer() {
        return players.get(me.getPlayerIndex());
    }

    public IngameStage getIngameStage() {
        return worldStage;
    }

}
