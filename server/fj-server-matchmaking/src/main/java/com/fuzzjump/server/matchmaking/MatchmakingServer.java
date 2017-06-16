package com.fuzzjump.server.matchmaking;

import com.fuzzjump.server.common.FuzzJumpMessageHandlers;
import com.fuzzjump.server.common.messages.lobby.Lobby;
import com.fuzzjump.server.matchmaking.lobby.LobbyPlayer;
import com.fuzzjump.server.matchmaking.lobby.LobbySession;
import com.steveadoo.server.base.Player;
import com.steveadoo.server.base.Server;
import com.steveadoo.server.base.ServerInfo;
import com.steveadoo.server.base.Session;
import com.steveadoo.server.common.packets.PacketHandler;
import io.netty.channel.Channel;

import javax.crypto.NoSuchPaddingException;
import java.net.InetSocketAddress;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.*;

public class MatchmakingServer extends Server {

    private static final int MAX_PLAYERS = 4;
    private static final int TICK = 500;

    private final GameServerTransferer gameServerTransferer;

    private ScheduledExecutorService matchService = Executors.newScheduledThreadPool(4);

    private ConcurrentHashMap<String, LobbySession> sessions = new ConcurrentHashMap<>();
    private ConcurrentLinkedQueue<LobbySession> openSessions = new ConcurrentLinkedQueue<>();


    public MatchmakingServer(ServerInfo serverInfo, InetSocketAddress gameServerAddress) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        super(serverInfo, new PacketHandler(FuzzJumpMessageHandlers.handlers), true);
        this.gameServerTransferer = new GameServerTransferer(gameServerAddress);
        getPacketHandler().addListener(Lobby.Loaded.class, this::lobbyLoaded);
        getPacketHandler().addListener(Lobby.ReadySet.class, this::readySet);
        getPacketHandler().addListener(Lobby.MapSlotSet.class, this::mapSlotSet);
    }

    private void readySet(LobbyPlayer player, Lobby.ReadySet message) {
        if (!checkSession(player))
            return;
        player.ready = message.getReady();
        player.session.setUpdate(true);
    }

    private void mapSlotSet(LobbyPlayer player, Lobby.MapSlotSet message) {
        if (!checkSession(player))
            return;
        player.mapId = message.getMapId();
        player.session.setUpdate(true);
    }

    private void lobbyLoaded(LobbyPlayer player, Lobby.Loaded message) {
        //TODO split this method up
        //player is in lobby and ready to find a game
        if (player.session != null)
            return;
        if (message.hasGameId()) {
            Session session = sessions.get(message.getGameId());
            if (session == null) {
                player.channel.writeAndFlush(Lobby.GameFound.newBuilder().setFound(false).buildPartial()).addListener((f) -> player.channel.disconnect());
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
            session.addPlayer(player);
            if (session.filled())
                openSessions.remove(session);
            player.channel.writeAndFlush(Lobby.GameFound.newBuilder().setFound(true).setGameId(session.id).setGameName(Server.MACHINE_NAME).buildPartial());
        }
    }

    private void updateSession(LobbySession lobbySession) {
        if (lobbySession.update()) {
            openSessions.remove(lobbySession);
            sessions.remove(lobbySession.id);
            //TODO send a status update to client
            gameServerTransferer.transfer(lobbySession);
        }
        if (lobbySession.end()) {
            lobbySession.future.cancel(false);
            lobbySession.getListener().ended();
        }
    }


    private boolean checkSession(LobbyPlayer player) {
        if (player.session == null) {
            player.channel.disconnect();
            return false;
        }
        return true;
    }


    @Override
    public Player createPlayer(Channel channel, long profileId) {
        return new LobbyPlayer(channel, profileId);
    }

    @Override
    public void connected(Player player) {
        System.out.println("Channel connected");
    }

    @Override
    public void disconnected(Player player) {
        if (player.session == null)
            return;
        player.session.removePlayer(player);
        if (!player.session.filled() && !openSessions.contains(player.session)) {
            openSessions.offer((LobbySession)player.session);
        }
    }

}
