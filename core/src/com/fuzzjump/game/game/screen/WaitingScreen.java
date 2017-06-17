package com.fuzzjump.game.game.screen;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.fuzzjump.game.FuzzJumpParams;
import com.fuzzjump.game.game.Assets;
import com.fuzzjump.game.game.player.Profile;
import com.fuzzjump.game.game.screen.ui.WaitingUI;
import com.fuzzjump.game.net.GameSession;
import com.fuzzjump.game.net.GameSessionWatcher;
import com.fuzzjump.libgdxscreens.StageScreen;
import com.fuzzjump.server.common.messages.lobby.Lobby;
import com.steveadoo.server.common.packets.PacketProcessor;

import java.util.ArrayList;

import javax.inject.Inject;

public class WaitingScreen extends StageScreen<WaitingUI> implements GameSessionWatcher {

    public static final int MAXIMUM_PLAYERS = 4;

    private final FuzzJumpParams params;

    private final Lobby.ReadySet.Builder readySetBuilder = Lobby.ReadySet.newBuilder();
    private final Lobby.MapSlotSet.Builder mapSlotSetBuilder = Lobby.MapSlotSet.newBuilder();

    private GameSession gameSession;

    private Dialog progressDialog;
    private Image image;
    private Label status;
    private Button closeButton;
    private Label timeLabel;

    private Profile[] newPlayers;
    private final Profile profile;

    private ArrayList<Profile> players = new ArrayList<>();

    @Inject
    public WaitingScreen(WaitingUI ui, FuzzJumpParams params, Profile profile) {
        super(ui);
        this.params = params;
        this.profile = profile;
    }

    @Override
    public void initialize() {
        progressDialog = getUI().actor(Dialog.class, Assets.WaitingUI.PROGRESS_DIALOG);
        image = getUI().actor(Image.class, Assets.WaitingUI.PROGRESS_IMAGE);
        status = getUI().actor(Label.class, Assets.WaitingUI.PROGRESS_LABEL);
        closeButton = getUI().actor(Button.class, Assets.WaitingUI.CLOSE_BUTTON);
        timeLabel = this.getUI().actor(Assets.WaitingUI.TIME_LABEL);
        image.setVisible(true);
        status.setVisible(true);
        closeButton.setVisible(false);
        status.setText("Finding game...");
        showDialog(progressDialog, getStage());
        gameSession = new GameSession(params.gameServerIp, params.gameServerPort, this);
        initPacketListeners();
        gameSession.connect();
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
        timeLabel.setText(String.format("%02d:%02d", time / 60, time % 60));
    }

    private void lobbyUpdate(GameSession session, Lobby.LobbyState message) {
        if (message.getPlayersCount() > 0) {
            players.clear();
            for (int i = 0; i < message.getPlayersCount(); i++) {
                Lobby.Player player = message.getPlayers(i);
                if (player.getProfileId() == profile.getProfileId()) {
                    profile.setPlayerIndex(player.getPlayerIndex());
                    profile.setReady(player.getReady());
                    players.add(profile);
                    continue;
                }
                Profile profile = findProfile(player);
                if (profile == null || profile.getProfileId() != player.getProfileId()) {
                    profile = new Profile();
                    profile.setPlayerIndex(player.getPlayerIndex());
                    profile.setProfileId(player.getProfileId());
                    profile.setReady(player.getReady());
                }
                profile.setReady(player.getReady());
                players.add(profile);
            }
            //getUI().(players);
        }
        //ui.setMapSlots(message.getMapSlotsList());
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

    }

    @Override
    public void showing() {

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
        progressDialog.setVisible(false);
        closeButton.setVisible(true);
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
