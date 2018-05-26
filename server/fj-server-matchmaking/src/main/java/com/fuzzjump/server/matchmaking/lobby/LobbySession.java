package com.fuzzjump.server.matchmaking.lobby;

import com.fuzzjump.server.base.FuzzJumpSession;
import com.fuzzjump.server.common.Maps;
import com.fuzzjump.server.common.messages.lobby.Lobby;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;


public class LobbySession extends FuzzJumpSession<LobbyPlayer> {

    private static final int LOBBY_WAIT_PERIOD = 8;

    private LobbySessionListener listener;

    private Lobby.LobbyState.Builder stateBuilder = Lobby.LobbyState.newBuilder();
    private Lobby.MapSlot.Builder mapSlotBuilder = Lobby.MapSlot.newBuilder();
    private Lobby.Player.Builder playerBuilder = Lobby.Player.newBuilder();
    private Lobby.TimeState.Builder timeStateBuilder = Lobby.TimeState.newBuilder();

    public ScheduledFuture<?> future;
    private final long waitPeriodEnd;

    public LobbySession(String id, int maxPlayers) {
        super(id, maxPlayers);
        update = true;
        setMaps();
        this.waitPeriodEnd = System.currentTimeMillis() + LOBBY_WAIT_PERIOD * 1000L;
    }

    private void setMaps() {
        Random random = new Random();
        List<Integer> mapIds = new ArrayList<>();
        for(int i = 0; i < Maps.MAP_CHOICE_COUNT; i++) {
            int id = 0;
            while(true) {
                id = random.nextInt(Maps.MAP_COUNT);
                if (mapIds.contains(id))
                    continue;
                mapIds.add(id);
                break;
            }
            stateBuilder.addMapSlots(mapSlotBuilder.setMapId(id).setVotes(0));
        }
    }

    @Override
    public boolean update() {
        int remainingTime = getRemainingTime();
        if (update) {
            update = false;
            stateBuilder.clearPlayers();
            for (LobbyPlayer player : players) {
                player.setSynced(false);
                playerBuilder.clear();
                playerBuilder.setPlayerIndex(player.getIndex());
                playerBuilder.setUserId(player.getUserId());
                playerBuilder.setReady(player.isReady());
                stateBuilder.addPlayers(playerBuilder.buildPartial());
            }
            for (int i = 0; i < stateBuilder.getMapSlotsCount(); i++) {
                Lobby.MapSlot.Builder bldr = stateBuilder.getMapSlotsBuilder(i);
                bldr.setVotes(0);
                for (LobbyPlayer player : players) {
                    if (player.getSelectedMap() == bldr.getMapId()) {
                        bldr.setVotes(bldr.getVotes() + 1);
                    }
                }
                stateBuilder.setMapSlots(i, bldr.buildPartial());
            }
        }
        timeStateBuilder.setTime(Math.round(remainingTime));
        Lobby.TimeState timeState = timeStateBuilder.build();
        stateBuilder.setTime(timeState);
        int readyCount = 0;
        for (LobbyPlayer player : players) {
            readyCount += player.isReady() ? 1 : 0;
            if (!player.isSynced()) {
                player.getChannel().write(stateBuilder.buildPartial());
                player.setSynced(true);
            } else {
                player.getChannel().write(timeState);
            }
            player.getChannel().flush();
        }
        return readyCount == 4 || remainingTime <= 0;
    }

    @Override
    public void destroy() {

    }

    public int getWinningMapId() {
        int[] maps = new int[Maps.MAP_COUNT];
        for(int i = 0; i < players.size(); i++) {
            LobbyPlayer player = players.get(i);
            if (player.getSelectedMap() != -1)
                maps[player.getSelectedMap()]++;
        }
        int mapId = -1;
        int votes = 0;
        for (int i = 0; i < maps.length; i++) {
            if (maps[i] > votes) {
                mapId = i;
                votes = maps[i];
            }
        }
        if (mapId == -1) {
            mapId = stateBuilder.getMapSlots(0).getMapId();
        }
        return mapId;
    }

    public boolean end() {
        return players.size() == 0;
    }

    public void setListener(LobbySessionListener listener) {
        this.listener = listener;
    }

    public LobbySessionListener getListener() {
        return listener;
    }

    public boolean getUpdate() {
        return update;
    }

    public int getRemainingTime() {
        return (int) (waitPeriodEnd - System.currentTimeMillis()) / 1000;
    }

    public interface LobbySessionListener {

        void ended();

    }
}
