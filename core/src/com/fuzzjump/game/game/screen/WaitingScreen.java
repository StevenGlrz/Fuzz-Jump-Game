package com.fuzzjump.game.game.screen;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.fuzzjump.api.session.ISessionService;
import com.fuzzjump.game.FuzzJumpParams;
import com.fuzzjump.game.game.Assets;
import com.fuzzjump.game.game.player.Profile;
import com.fuzzjump.game.game.screen.ui.WaitingUI;
import com.fuzzjump.game.net.GameSession;
import com.fuzzjump.game.net.GameSessionWatcher;
import com.fuzzjump.game.util.GraphicsScheduler;
import com.fuzzjump.libgdxscreens.screen.StageScreen;
import com.fuzzjump.server.common.messages.lobby.Lobby;
import com.steveadoo.server.common.packets.PacketProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class WaitingScreen extends StageScreen<WaitingUI> implements GameSessionWatcher {

    public static final int MAX_PLAYERS = 4;

    private final FuzzJumpParams params;
    private final ISessionService sessionService;
    private final GraphicsScheduler scheduler;

    private final Stage stage;

    private final Lobby.ReadySet.Builder readySetBuilder = Lobby.ReadySet.newBuilder();
    private final Lobby.MapSlotSet.Builder mapSlotSetBuilder = Lobby.MapSlotSet.newBuilder();

    private GameSession gameSession;

    private Label timeLabel;

    private Profile[] newPlayers;
    private final Profile profile;

    private List<Profile> players = new ArrayList<>();

    private String matchmakingToken;

    @Inject
    public WaitingScreen(Stage stage,
                         WaitingUI ui,
                         FuzzJumpParams params,
                         Profile profile,
                         ISessionService sessionService,
                         GraphicsScheduler scheduler) {
        super(ui);
        this.stage = stage;
        this.params = params;
        this.profile = profile;
        this.sessionService = sessionService;
        this.scheduler = scheduler;
    }

    @Override
    public void onReady() {
        timeLabel = ui().actor(Assets.WaitingUI.TIME_LABEL);
        gameSession = new GameSession(params.matchmakingIp, params.matchmakingPort, this);

        sessionService.getSessionToken("MATCHMAKING")
                .observeOn(scheduler)
                .subscribe(response -> {
                    WaitingScreen.this.matchmakingToken = response.getBody();
                    gameSession.connect();
                    initPacketListeners();
                });

    }

    private void initPacketListeners() {
        PacketProcessor packetProcessor = gameSession.getPacketProcessor();
        packetProcessor.addListener(Lobby.GameFound.class, this::gameFound);
        packetProcessor.addListener(Lobby.LobbyState.class, this::lobbyUpdate);
        packetProcessor.addListener(Lobby.TimeState.class, this::updateTime);
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
                if (player.getUserId().equals(profile.getUserId())) {
                    profile.setPlayerIndex(player.getPlayerIndex());
                    profile.setReady(player.getReady());
                    players.add(profile);
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
                players.add(profile);
            }
            ui().update(players);
        }
        ui().setMapSlots(message.getMapSlotsList());
        int time = message.getTime().getTime();
        setTime(time);
    }

    private Profile findProfile(Lobby.Player player) {
        for (int i = 0; i < players.size(); i++) {
            Profile profile = players.get(i);
            if (player.getPlayerIndex() == profile.getPlayerIndex()) {
                return profile;
            }
        }
        return null;
    }

    private void gameFound(GameSession session, Lobby.GameFound message) {
        System.out.println("Game found message");
    }

    @Override
    public void onShow() {
    }

    @Override
    public void clicked(int id, Actor actor) {
        switch (id) {
            case Assets.WaitingUI.CANCEL_BUTTON:
                gameSession.close(true);
                screenHandler.showScreen(MenuScreen.class);
                break;
            case Assets.WaitingUI.READY_BUTTON:
                gameSession.send(readySetBuilder.setReady(!profile.isReady()).build());
                break;
            case Assets.WaitingUI.MAP_BUTTON:
                gameSession.send(mapSlotSetBuilder.setMapId((int) actor.getUserObject()).build());
                break;
        }
    }

    @Override
    public void onConnect() {
        Lobby.Loaded loadedMessage = Lobby.Loaded.newBuilder().buildPartial();
        gameSession.send(loadedMessage);
    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onTimeout() {

    }
}
