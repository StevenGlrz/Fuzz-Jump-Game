package com.fuzzjump.server.matchmaking;

import com.fuzzjump.api.Api;
import com.fuzzjump.server.base.FuzzJumpServer;
import com.fuzzjump.server.common.FuzzJumpMessageHandlers;
import com.fuzzjump.server.common.messages.join.Join;
import com.fuzzjump.server.common.messages.lobby.Lobby;
import com.fuzzjump.server.matchmaking.lobby.LobbyPlayer;
import com.fuzzjump.server.matchmaking.lobby.LobbySession;
import com.steveadoo.server.common.packets.PacketProcessor;

import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;

public class MatchmakingServer extends FuzzJumpServer<LobbyPlayer, MatchmakingServerInfo> {

    private static final int MAX_PLAYERS = 4;
    private static final int TICK = 500;

    //private final GameServerTransferer gameServerTransferer;

    private ScheduledExecutorService matchService = Executors.newScheduledThreadPool(4);

    private ConcurrentHashMap<String, LobbySession> sessions = new ConcurrentHashMap<>();
    private ConcurrentLinkedQueue<LobbySession> openSessions = new ConcurrentLinkedQueue<>();


    public MatchmakingServer(MatchmakingServerInfo serverInfo) {
        super(serverInfo, new PacketProcessor(FuzzJumpMessageHandlers.HANDLERS));
        addValidator(new MatchmakingValidator(this));
        //this.gameServerTransferer = new GameServerTransferer(gameServerAddress);
        getPacketProcessor().addListener(Lobby.Loaded.class, this::lobbyLoaded);
        getPacketProcessor().addListener(Lobby.ReadySet.class, this::readySet);
        getPacketProcessor().addListener(Lobby.MapSlotSet.class, this::mapSlotSet);
    }

    private void readySet(LobbyPlayer player, Lobby.ReadySet message) {
        if (!checkSession(player))
            return;
        player.setReady(message.getReady());
        player.getSession().setUpdate(true);
    }

    private void mapSlotSet(LobbyPlayer player, Lobby.MapSlotSet message) {
        if (!checkSession(player))
            return;
        player.setMapId(message.getMapId());
        player.getSession().setUpdate(true);
    }

    private void lobbyLoaded(LobbyPlayer player, Lobby.Loaded message) {
        //TODO split this method up
        //player is in lobby and ready to find a game
        if (player.getSession() != null)
            return;
        if (message.hasGameId()) {
            LobbySession session = sessions.get(message.getGameId());
            if (session == null) {
                player.getChannel().writeAndFlush(Lobby.GameFound.newBuilder().setFound(false).buildPartial()).addListener((f) -> player.getChannel().disconnect());
            } else {
                session.addPlayer(player);
            }
        } else if (message.hasPrivate() && message.getPrivate()) {
            LobbySession session = new LobbySession(UUID.randomUUID().toString(), MAX_PLAYERS, false);
            session.addPlayer(player);
            sessions.put(session.id, session);
        } else {
            findOpenSession(player);
        }
    }

    private void findOpenSession(LobbyPlayer player) {
        synchronized (openSessions) {
            LobbySession session = null;
            Iterator<LobbySession> iterator = openSessions.iterator();
            while (true) {
                session = iterator.hasNext() ? iterator.next() : null;
                if (session == null) {
                    session = new LobbySession(UUID.randomUUID().toString(), MAX_PLAYERS, false);
                    openSessions.add(session);
                    sessions.put(session.id, session);
                    final LobbySession lobbySession = session;
                    lobbySession.future = matchService.scheduleAtFixedRate(() -> updateSession(lobbySession), TICK, TICK, TimeUnit.MILLISECONDS);
                    break;
                }
                if (!session.filled()) {
                    break;
                }
            }
            System.out.println("Found session for player " + player.getUserId());
            session.addPlayer(player);
            System.out.println("Lobby should update? " + session.getUpdate());
            if (session.filled())
                openSessions.remove(session);
            player.getChannel().writeAndFlush(Lobby.GameFound.newBuilder()
                    .setFound(true)
                    .setGameId(session.id)
                    .setGameName("GAME")
                    .buildPartial());
        }
    }

    private void updateSession(LobbySession lobbySession) {
        try {
            if (lobbySession.update()) {
                openSessions.remove(lobbySession);
                sessions.remove(lobbySession.id);
                //TODO send a status update to client
                //gameServerTransferer.transfer(lobbySession);
            }
            if (lobbySession.end()) {
                lobbySession.future.cancel(true);
                if (lobbySession.getListener() != null) {
                    lobbySession.getListener().ended();
                }
                openSessions.remove(lobbySession);
                sessions.remove(lobbySession.id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //if this exception throws the whole tick will stop.
        }
    }


    private boolean checkSession(LobbyPlayer player) {
        if (player.getSession() == null) {
            player.getChannel().disconnect();
            return false;
        }
        return true;
    }


    @Override
    public LobbyPlayer createPlayer(Channel channel) {
        return new LobbyPlayer(channel);
    }

    @Override
    public void connected(LobbyPlayer player) {
        System.out.println("Channel connected");
    }

    @Override
    public void disconnected(LobbyPlayer player) {
        if (player.getSession() == null)
            return;
        player.getSession().removePlayer(player);
        if (!player.getSession().filled() && !openSessions.contains(player.getSession())) {
            openSessions.offer((LobbySession)player.getSession());
        }
    }

}
