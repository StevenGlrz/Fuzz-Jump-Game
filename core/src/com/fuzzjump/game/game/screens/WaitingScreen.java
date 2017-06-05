package com.fuzzjump.game.game.screens;

import android.util.SparseArray;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.fuzzjump.game.FuzzJump;
import com.fuzzjump.game.game.ScreenHandler;
import com.fuzzjump.game.game.StageIds;
import com.fuzzjump.game.game.StageScreen;
import com.fuzzjump.game.game.StageUI;
import com.fuzzjump.game.game.Textures;
import com.fuzzjump.game.game.screens.attachment.GameScreenAttachment;
import com.fuzzjump.game.game.screens.attachment.MenuScreenAttachment;
import com.fuzzjump.game.game.screens.attachment.ScreenAttachment;
import com.fuzzjump.game.game.screens.attachment.WaitingScreenAttachment;
import com.fuzzjump.game.model.profile.PlayerProfile;
import com.fuzzjump.game.net.GameSession;
import com.fuzzjump.game.net.GameSessionWatcher;
import com.fuzzjump.game.net.requests.GetAppearanceRequest;
import com.fuzzjump.game.net.requests.WebRequest;
import com.fuzzjump.game.net.requests.WebRequestCallback;
import com.kerpowgames.fuzzjump.common.Lobby;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WaitingScreen extends StageScreen<WaitingScreenAttachment> implements GameSessionWatcher {

    public static final int MAXIMUM_PLAYERS = 4;

    private com.fuzzjump.game.game.ui.WaitingUI ui;
    private GameSession session;
    private GetAppearanceRequest getAppearanceRequest;

    private WebRequestCallback getAppearanceCallback = new WebRequestCallback() {
        @Override
        public void onResponse(JSONObject response) {
            updateAppearances(response);
        }
    };

    private Lobby.ReadySet.Builder readySetBuilder = Lobby.ReadySet.newBuilder();
    private Lobby.MapSlotSet.Builder mapSlotSetBuilder = Lobby.MapSlotSet.newBuilder();

    private SparseArray<PlayerProfile> players = new SparseArray<>();
    private PlayerProfile[] newPlayers;

    private Dialog progressDialog;
    private Image image;
    private Label status;
    private Button closeButton;

    public WaitingScreen(FuzzJump game, Textures textures, ScreenHandler handler) {
        super(game, textures, handler);
    }

    @Override
    public void load(WaitingScreenAttachment attachment) {
        this.ui = (com.fuzzjump.game.game.ui.WaitingUI) super.ui();
        this.session = attachment.getSession();
        this.session.setGameWatcher(this);

        this.progressDialog = ui().actor(Dialog.class, StageIds.WaitingUI.PROGRESS_DIALOG);
        this.image = ui().actor(Image.class, StageIds.WaitingUI.PROGRESS_IMAGE);
        this.status = ui().actor(Label.class, StageIds.WaitingUI.PROGRESS_LABEL);
        this.closeButton = ui().actor(Button.class, StageIds.WaitingUI.CLOSE_BUTTON);
        image.setVisible(true);
        status.setVisible(true);
        closeButton.setVisible(false);
        status.setText("Finding game...");
        progressDialog.show(game.getStage());

        session.getPacketHandler().addListener(Lobby.GameFound.class, new PacketHandler.PacketListener<GameSession, Lobby.GameFound>() {
            @Override
            public void received(GameSession gameSession, Lobby.GameFound message) {
                gameFound(session, message);
            }
        });
        session.getPacketHandler().addListener(Lobby.LobbyState.class, new PacketHandler.PacketListener<GameSession, Lobby.LobbyState>() {
            @Override
            public void received(GameSession gameSession, Lobby.LobbyState lobbyState) {
                lobbyUpdate(session, lobbyState);
            }
        });
        session.getPacketHandler().addListener(Lobby.TimeState.class, new PacketHandler.PacketListener<GameSession, Lobby.TimeState>() {
            @Override
            public void received(GameSession gameSession, Lobby.TimeState timeState) {
                setTime(timeState.getTime());
            }
        });
        session.getPacketHandler().addListener(Lobby.GameServerSetupData.class, new PacketHandler.PacketListener<GameSession, Lobby.GameServerSetupData>() {
            @Override
            public void received(GameSession gameSession, Lobby.GameServerSetupData gameServerSetupData) {
                gameServerSetup(gameSession, gameServerSetupData);
            }
        });
        session.getPacketHandler().addListener(Lobby.GameServerFound.class, new PacketHandler.PacketListener<GameSession, Lobby.GameServerFound>() {
            @Override
            public void received(GameSession gameSession, Lobby.GameServerFound gameServerFound) {
                gameServerFound(gameSession, gameServerFound);
            }
        });
        session.getPacketHandler().addListener(Lobby.FindingGame.class, new PacketHandler.PacketListener<GameSession, Lobby.FindingGame>() {
            @Override
            public void received(GameSession gameSession, Lobby.FindingGame findingGame) {
                findingGame(gameSession, findingGame);
            }
        });
        Lobby.Loaded loadedMessage = Lobby.Loaded.newBuilder().buildPartial();
        this.session.send(loadedMessage);
    }

    private void findingGame(GameSession gameSession, Lobby.FindingGame message) {
        if (message.getFinding()) {
            status.setVisible(true);
            closeButton.setVisible(false);
            image.setVisible(true);
            System.out.println("Finding gameserver");
            status.setText("Finding game server");
            progressDialog.show(game.getStage());
        } else {
            screenHandler.showScreen(MenuScreen.class, new MenuScreenAttachment(false, true));
            session.close(true);
        }
    }

    private void gameServerFound(GameSession gameSession, Lobby.GameServerFound message) {
        if (message.getFound()) {
            System.out.println("Found gameserver");
            status.setVisible(true);
            closeButton.setVisible(false);
            image.setVisible(true);
            status.setText("Found server");
        }
    }

    private void gameServerSetup(GameSession gameSession, Lobby.GameServerSetupData message) {
        System.out.println("Received server setup data");
        status.setVisible(true);
        closeButton.setVisible(false);
        image.setVisible(true);
        status.setText("Joining");
        GameScreenAttachment attachment = new GameScreenAttachment(message.getMapId(), message.getSeed(), message.getIp(), message.getPort(), message.getKey(), message.getGameId(), 1);
        screenHandler.showScreen(GameScreen.class, attachment);
        session.close(true);
    }

    private void gameFound(GameSession session, Lobby.GameFound message) {
        image.setVisible(false);
        status.setText("Connected to " + message.getGameName());
        progressDialog.hide();
    }

    private void lobbyUpdate(GameSession session, Lobby.LobbyState message) {
        synchronized (getAppearanceCallback) {
            if (message.getPlayersCount() > 0) {
                newPlayers = new PlayerProfile[message.getPlayersCount() - 1];
                int arrIndex = 0;
                for (int i = 0; i < message.getPlayersCount(); i++) {
                    Lobby.Player player = message.getPlayers(i);
                    if (player.getProfileId() == game.getProfile().getProfileId()) {
                        System.out.println("Found our player profile");
                        game.getProfile().setPlayerIndex(player.getPlayerIndex());
                        game.getProfile().setReady(player.getReady());
                        continue;
                    }
                    PlayerProfile profile = players.get(player.getPlayerIndex());
                    if (profile == null || profile.getProfileId() != player.getProfileId()) {
                        profile = new PlayerProfile(player);
                    } else {
                        profile.setReady(player.getReady());
                    }
                    newPlayers[arrIndex++] = profile;
                }
                players.clear();
                players.put(game.getProfile().getPlayerIndex(), game.getProfile());
                for (int i = 0; i < newPlayers.length; i++) {
                    PlayerProfile profile = newPlayers[i];
                    players.put(profile.getPlayerIndex(), profile);
                }
                ui.setPlayers(players);
                downloadAppearances();
            }
            ui.setMapSlots(message.getMapSlotsList());
        }
        int time = message.getTime().getTime();
        setTime(time);
    }

    public void setTime(int time) {
        Label timeLabel = ui().actor(StageIds.WaitingUI.TIME_LABEL);
        timeLabel.setText(String.format("%02d:%02d", time / 60, time % 60));
    }

    public void downloadAppearances() {
        if (getAppearanceRequest != null) {
            getAppearanceRequest.cancel();
        }
        ArrayList<Long> ids = new ArrayList<Long>();
        for (int i = 0; i < players.size(); i++) {
            PlayerProfile profile = players.valueAt(i);
            if (profile == null || profile.getAppearance().loaded())
                continue;
            ids.add(profile.getProfileId());
        }
        if (ids.size() == 0)
            return;
        long[] ihatejava = new long[ids.size()];
        for (int i = 0; i < ids.size(); i++)
            ihatejava[i] = ids.get(i);
        getAppearanceRequest = new GetAppearanceRequest(ihatejava, false);
        getAppearanceRequest.connect(getAppearanceCallback);
    }

    public void updateAppearances(JSONObject response) {
        synchronized (getAppearanceCallback) {
            try {
                if (response.has(WebRequest.RESPONSE_KEY) && response.getInt(WebRequest.RESPONSE_KEY) == WebRequest.SUCCESS) {
                    JSONArray payload = response.getJSONArray(WebRequest.PAYLOAD_KEY);
                    for (int i = 0; i < payload.length(); i++) {
                        JSONObject appearance = payload.getJSONObject(i);
                        System.out.println(appearance.toString());
                        long profileId = appearance.getLong("ProfileId");
                        PlayerProfile profile = getProfile(profileId);
                        if (profile == null)
                            continue;
                        profile.setName(appearance.getString("DisplayName"));
                        profile.getAppearance().load(appearance);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                getAppearanceRequest = null;
            }
        }
    }

    public PlayerProfile getProfile(long profileId) {
        for(int i = 0; i < players.size(); i++) {
            PlayerProfile profile = players.valueAt(i);
            if (profile.getProfileId() == profileId)
                return profile;
        }
        return null;
    }

    @Override
    public void showScreen() {
    }

    public void onConnect() {
    }

    @Override
    public void onTransferred() {
    }

    @Override
    public void onDisconnect() {
        screenHandler.showScreen(MenuScreen.class, new MenuScreenAttachment(false, true));
    }

    @Override
    public void onTimeout() {
        screenHandler.showScreen(MenuScreen.class, new MenuScreenAttachment(false, true));
    }

    @Override
    public void authenticated() {

    }

    @Override
    public StageUI createUI() {
        return new com.fuzzjump.game.game.ui.WaitingUI();
    }

    @Override
    public void clicked(int id, Actor actor) {
        switch (id) {
            case StageIds.WaitingUI.READY_BUTTON:
                session.send(readySetBuilder.setReady(!game.getProfile().isReady()).build());
                break;
            case StageIds.WaitingUI.CANCEL_BUTTON:
                session.close(true);
                screenHandler.showScreen(MenuScreen.class, new MenuScreenAttachment(false, false));
                break;
            case StageIds.WaitingUI.MAP_BUTTON:
                session.send(mapSlotSetBuilder.setMapId((int) actor.getUserObject()).build());
                break;
        }
    }

}
