package com.fuzzjump.game.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.IntMap;
import com.fuzzjump.api.profile.IProfileService;
import com.fuzzjump.api.session.ISessionService;
import com.fuzzjump.game.FuzzJumpParams;
import com.fuzzjump.game.game.Assets;
import com.fuzzjump.game.game.FuzzContext;
import com.fuzzjump.game.game.player.Profile;
import com.fuzzjump.game.game.screen.core.ProfileFetcher;
import com.fuzzjump.game.game.screen.ui.WaitingUI;
import com.fuzzjump.game.net.GameSession;
import com.fuzzjump.game.net.GameSessionWatcher;
import com.fuzzjump.game.util.GraphicsScheduler;
import com.fuzzjump.libgdxscreens.screen.StageScreen;
import com.fuzzjump.server.common.messages.join.Join;
import com.fuzzjump.server.common.messages.lobby.Lobby;

import java.util.Locale;

import javax.inject.Inject;

public class WaitingScreen extends StageScreen<WaitingUI> implements GameSessionWatcher {

    public static final int MAX_PLAYERS = 4;

    private final FuzzJumpParams params;
    private final ISessionService sessionService;
    private final IProfileService profileService;
    private final GraphicsScheduler scheduler;
    private final FuzzContext context;
    private final ProfileFetcher profileFetcher;

    private final Stage stage;

    private final Lobby.ReadySet.Builder readySetBuilder = Lobby.ReadySet.newBuilder();
    private final Lobby.MapSlotSet.Builder mapSlotSetBuilder = Lobby.MapSlotSet.newBuilder();

    private GameSession gameSession;

    private Label timeLabel;
    private Dialog connectingDialog;
    private TextButton connectingButton;
    private Actor connectingProgress;
    private Label connectingMessage;
    private Table mapTable;

    private final Profile myProfile;

    private IntMap<Profile> players = new IntMap<>();

    private String matchmakingKey;
    private Join.JoinResponsePacket joinResponse;

    @Inject
    public WaitingScreen(Stage stage,
                         FuzzContext context,
                         WaitingUI ui,
                         FuzzJumpParams params,
                         Profile profile,
                         ISessionService sessionService,
                         IProfileService profileService,
                         GraphicsScheduler scheduler,
                         ProfileFetcher profileFetcher) {
        super(ui);
        this.stage = stage;
        this.context = context;
        this.params = params;
        this.myProfile = profile;
        this.sessionService = sessionService;
        this.profileService = profileService;
        this.scheduler = scheduler;
        this.profileFetcher = profileFetcher;
    }

    @Override
    public void onReady() {
        timeLabel = ui().actor(Assets.WaitingUI.TIME_LABEL);
        connectingDialog = ui().actor(Assets.WaitingUI.CONNECTING_DIALOG);
        connectingButton = ui().actor(Assets.WaitingUI.CONNECTING_BUTTON);
        connectingMessage = ui().actor(Assets.WaitingUI.CONNECTING_MESSAGE);
        connectingProgress = ui().actor(Assets.WaitingUI.CONNECTING_PROGRESS);
        mapTable = ui().actor(Assets.WaitingUI.MAP_TABLE);
    }

    @Override
    public void onShow() {
        mapTable.setVisible(false);
        connectingButton.setText("Cancel");
        connectingMessage.setText("Connecting");
        connectingProgress.setVisible(true);
        showDialog(connectingDialog, getStage());
        gameSession = new GameSession(params.matchmakingIp, params.matchmakingPort, this);

        sessionService.getSessionToken("MATCHMAKING")
                .observeOn(scheduler)
                .subscribe(response -> {
                    if (gameSession == null) {
                        return;
                    }
                    matchmakingKey = response.getBody();
                    gameSession.connect();
                    initPacketListeners();
                });

    }

    @Override
    public void clicked(int id, Actor actor) {
        switch (id) {
            case Assets.WaitingUI.CONNECTING_BUTTON:
            case Assets.WaitingUI.CANCEL_BUTTON:
                cancel();
                break;
            case Assets.WaitingUI.READY_BUTTON:
                if (gameSession == null) {
                    return;
                }
                gameSession.send(readySetBuilder.setReady(!myProfile.isReady()).build());
                break;
            case Assets.WaitingUI.MAP_BUTTON:
                if (gameSession == null) {
                    return;
                }
                gameSession.send(mapSlotSetBuilder.setMapId((int) actor.getUserObject()).build());
                break;
        }
    }

    private void initPacketListeners() {
        gameSession.getPacketProcessor().addListener(Join.JoinResponsePacket.class, this::joinResponse)
            .addListener(Lobby.GameFound.class, this::gameFound)
            .addListener(Lobby.LobbyState.class, this::lobbyUpdate)
            .addListener(Lobby.TimeState.class, this::updateTime)
            .addListener(Lobby.GameServerSetupData.class, this::gameServerFound);
    }

    private void gameServerFound(GameSession sender, Lobby.GameServerSetupData message) {
        gameSession.close(true);
        gameSession = null;
        System.out.println("Game server details: " + message.toString());
        context.setGameId(message.getGameId());
        context.setGameMap(message.getMapId());
        context.setGameSeed(message.getSeed());
        context.setSessionKey(message.getKey());
        context.setIp(message.getIp());
        context.setPort(message.getPort());
        screenHandler.showScreen(GameScreen.class);
    }

    private void joinResponse(GameSession session, Join.JoinResponsePacket packet) {
        connectingMessage.setText("Finding game");
        this.joinResponse = packet;
        Lobby.Loaded loadedPacket = Lobby.Loaded.newBuilder().build();
        gameSession.send(loadedPacket);
    }

    private void updateTime(GameSession session, Lobby.TimeState message) {
        setTime(message.getTime());
    }

    private void setTime(int time) {
        timeLabel.setText(String.format(Locale.US, "%02d:%02d", time / 60, time % 60));
    }

    private void lobbyUpdate(GameSession session, Lobby.LobbyState message) {
        if (message.getPlayersCount() > 0) {
            players.clear();
            for (int i = 0; i < message.getPlayersCount(); i++) {
                Lobby.Player player = message.getPlayers(i);
                if (player.getUserId().equals(myProfile.getUserId())) {
                    myProfile.setPlayerIndex(player.getPlayerIndex());
                    myProfile.setReady(player.getReady());
                    players.put(myProfile.getPlayerIndex(), myProfile);
                    continue;
                }
                Profile profile = findProfile(player);
                if (profile == null || !profile.getUserId().equals(player.getUserId())) {
                    profile = new Profile();
                    profile.setPlayerIndex(player.getPlayerIndex());
                    profile.setUserId(player.getUserId());
                    profile.setReady(player.getReady());
                }
                profile.setReady(player.getReady());
                players.put(profile.getPlayerIndex(), profile);
            }
            ui().update(players);
            profileFetcher.fetchPlayerProfiles(() -> players.values().toArray(), () -> ui().update(players));
        }
        ui().setMapSlots(message.getMapSlotsList());
        int time = message.getTime().getTime();
        setTime(time);
        if (!mapTable.isVisible()) {
            mapTable.setVisible(true);
            connectingDialog.setVisible(false);
        }
    }

    private Profile findProfile(Lobby.Player player) {
        return players.get(player.getPlayerIndex());
    }

    private void gameFound(GameSession session, Lobby.GameFound message) {
        if (message.getFound()) {
            connectingMessage.setText("Found game");
        } else {
            connectingMessage.setText("Bad game id");
            connectingProgress.setVisible(false);
        }
    }

    private void cancel() {
        connectingButton.setVisible(false);
        connectingMessage.setText("Leaving");
        if (mapTable.isVisible()) {
            connectingDialog.setVisible(true);
        }
        //let the dialog show up, then disconnect
        Gdx.app.postRunnable(() -> {
            if (gameSession != null) {
                gameSession.close(true);
            }
            gameSession = null;
            screenHandler.showScreen(MenuScreen.class);
        });
    }

    @Override
    public void onConnect() {
        connectingMessage.setText("Validating myProfile");

        System.out.println(matchmakingKey + ", " + myProfile.getUserId());
        Join.JoinPacket joinPacket = Join.JoinPacket.newBuilder()
                .setUserId(myProfile.getUserId())
                .setSessionKey(matchmakingKey)
                .setVersion(1)
                .build();
        gameSession.send(joinPacket);
    }

    @Override
    public void onDisconnect() {
        if (gameSession == null) {
            return;
        }
        if (!connectingDialog.isVisible()) {
            connectingDialog.setVisible(true);
        }
        connectingMessage.setText("Network error");
        connectingButton.setText("Leave");
        connectingButton.setVisible(true);
        connectingProgress.setVisible(false);
    }

}
