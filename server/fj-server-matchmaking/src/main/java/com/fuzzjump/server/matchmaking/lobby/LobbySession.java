package com.fuzzjump.server.matchmaking.lobby;

import com.fuzzjump.server.base.FuzzJumpSession;
import com.fuzzjump.server.common.messages.lobby.Lobby;
import com.fuzzjump.server.common.Maps;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;


public class LobbySession extends FuzzJumpSession<LobbyPlayer> {

    private LobbySessionListener listener;

    private Lobby.LobbyState.Builder stateBuilder = Lobby.LobbyState.newBuilder();
    private Lobby.MapSlot.Builder mapSlotBuilder = Lobby.MapSlot.newBuilder();
    private Lobby.Player.Builder playerBuilder = Lobby.Player.newBuilder();
    private Lobby.TimeState.Builder timeStateBuilder = Lobby.TimeState.newBuilder();

    private float remainingTime = 60;

    public ScheduledFuture<?> future;

    public LobbySession(String id, int maxPlayers, boolean timerFill) {
        super(id, maxPlayers);
        update = true;
        setMaps();
    }

    private void setMaps() {
        Random random = new Random();
        ArrayList<Integer> mapIds = new ArrayList<>();
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

    long lastTime = System.currentTimeMillis();

    @Override
    public boolean update() {
        long time = System.currentTimeMillis() - lastTime;
        lastTime = System.currentTimeMillis();
        remainingTime -= time / 1000f;
        if (update) {
            stateBuilder.clearPlayers();
            for (LobbyPlayer player : players) {
                player.setSynced(false);
                playerBuilder.clear();
                playerBuilder.setPlayerIndex(player.getIndex());
                playerBuilder.setProfileId(player.getProfileId());
                playerBuilder.setReady(player.isReady());
                stateBuilder.addPlayers(playerBuilder.buildPartial());
            }
            for (int i = 0; i < stateBuilder.getMapSlotsCount(); i++) {
                Lobby.MapSlot.Builder bldr = stateBuilder.getMapSlotsBuilder(i);
                bldr.setVotes(0);
                for (LobbyPlayer player : players) {
                    if (player.getMapId() == bldr.getMapId()) {
                        bldr.setVotes(bldr.getVotes() + 1);
                    }
                }
                stateBuilder.setMapSlots(i, bldr.buildPartial());
            }
            update = false;
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
        return remainingTime <= 0;
    }

    @Override
    public void destroy() {

    }

    public int getWinningMapId() {
        int[] maps = new int[Maps.MAP_COUNT];
        for(int i = 0; i < players.size(); i++) {
            LobbyPlayer player = players.get(i);
            if (player.getMapId() != -1)
                maps[player.getMapId()]++;
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

    public interface LobbySessionListener {

        void ended();

    }
}
