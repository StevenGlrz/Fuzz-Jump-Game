package com.fuzzjump.server.matchmaking;

import com.fuzzjump.server.base.FuzzJumpServer;
import com.fuzzjump.server.common.FuzzJumpMessageHandlers;
import com.fuzzjump.server.common.messages.lobby.Lobby;
import com.fuzzjump.server.matchmaking.lobby.LobbyPlayer;
import com.fuzzjump.server.matchmaking.lobby.LobbySession;
import com.steveadoo.server.common.packets.PacketProcessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

    private final GameServerTransferer gameServerTransferer;

    private ScheduledExecutorService matchService = Executors.newScheduledThreadPool(4);

    private ConcurrentHashMap<String, LobbySession> sessions = new ConcurrentHashMap<>();
    private ConcurrentLinkedQueue<LobbySession> openSessions = new ConcurrentLinkedQueue<>();

    private List<LobbySession> sessionsToRemove = new ArrayList<>();

    public MatchmakingServer(MatchmakingServerInfo serverInfo) {
        super(serverInfo, new PacketProcessor(FuzzJumpMessageHandlers.HANDLERS));
        addValidator(new MatchmakingValidator(this));
        this.gameServerTransferer = new GameServerTransferer(this);
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
            LobbySession session = new LobbySession(UUID.randomUUID().toString(), MAX_PLAYERS);
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
                    session = new LobbySession(UUID.randomUUID().toString(), MAX_PLAYERS);
                    openSessions.add(session);
                    sessions.put(session.id, session);
                    final LobbySession lobbySession = session;
                    lobbySession.future = matchService.scheduleAtFixedRate(() -> updateSession(lobbySession), TICK, TICK, TimeUnit.MILLISECONDS);
                    break;
                }
                if (session.getRemainingTime() <= 0) {
                    sessionsToRemove.add(session);
                    continue;
                }
                if (!session.filled()) {
                    break;
                }
            }

            for(int i = 0; i < sessionsToRemove.size(); i++) {
                LobbySession sessionToRemove = sessionsToRemove.get(i);
                openSessions.remove(sessionToRemove);
                sessions.remove(sessionToRemove.id);
            }
            sessionsToRemove.clear();

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
                lobbySession.future.cancel(true);
                if (lobbySession.getListener() != null) {
                    lobbySession.getListener().ended();
                }
                //transfers clients to a gameserver.
                //the transfer logic will handle disconnecting the players from matchmaking
                gameServerTransferer.transfer(lobbySession);
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
        LobbySession session = (LobbySession) player.getSession();
        session.removePlayer(player);
        if (session.getRemainingTime() > 0 && !player.getSession().filled() && !openSessions.contains(player.getSession())) {
            openSessions.offer((LobbySession)player.getSession());
        }
    }

}
