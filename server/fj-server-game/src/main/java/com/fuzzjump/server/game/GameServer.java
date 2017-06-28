package com.fuzzjump.server.game;

import com.fuzzjump.server.base.FuzzJumpServer;
import com.fuzzjump.server.common.FuzzJumpMessageHandlers;
import com.fuzzjump.server.common.messages.game.Game;
import com.fuzzjump.server.common.messages.lobby.Lobby;
import com.fuzzjump.server.game.game.GamePlayer;
import com.fuzzjump.server.game.game.GameSession;
import com.steveadoo.server.common.packets.PacketProcessor;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;

public class GameServer extends FuzzJumpServer<GamePlayer, GameServerConfig> {

    private static final int TICK = 100;

    private final GameServerPlayerValidator gameServerValidator;
    private final ScheduledExecutorService gameService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    private final ConcurrentHashMap<String, GameSession> sessions = new ConcurrentHashMap<>();


    public GameServer(GameServerConfig serverInfo) {
        super(serverInfo, new PacketProcessor(FuzzJumpMessageHandlers.HANDLERS));
        gameServerValidator = new GameServerPlayerValidator(this);
        addValidator(gameServerValidator);
        addValidator(new GameServerMatchmakingValidator(this));
        getPacketProcessor().addListener(Lobby.GameServerSetup.class, this::onGameServerSetup);
        getPacketProcessor().addListener(Game.JoinGame.class, this::onJoinGame);
        getPacketProcessor().addListener(Game.Loaded.class, this::onGameLoaded);
    }

    private void onJoinGame(GamePlayer player, Game.JoinGame message) {
        try {
            String key = message.getGameId();
            if (!sessions.containsKey(key)) {
                player.getChannel().writeAndFlush(Game.JoinGameResponse.newBuilder().setFound(false).buildPartial());
            } else {
                player.getChannel().writeAndFlush(Game.JoinGameResponse.newBuilder().setFound(true).buildPartial());
                GameSession session = sessions.get(key);
                session.addPlayer(player);
                checkSessionStart(session, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            player.getChannel().disconnect();
        }
    }

    private void onGameLoaded(GamePlayer player, Game.Loaded message) {
        try {
            player.setLoaded(true);
            checkSessionLoaded((GameSession) player.getSession(), false);
        } catch (Exception e) {
            e.printStackTrace();
            player.getChannel().disconnect();
        }
    }

    private void onGameServerSetup(GamePlayer player, Lobby.GameServerSetup message) {
        if (!player.isServer()) {
            player.getChannel().disconnect();
            return;
        }
        int keyCount = message.getPlayerCount();
        String[] keys = gameServerValidator.generateSessionKeys(keyCount);
        String gameId = UUID.randomUUID().toString();
        GameSession session = new GameSession(message.getMapId(), gameId, message.getPlayerCount());
        sessions.put(gameId, session);
        Lobby.GameServerSetupResponse.Builder builder = Lobby.GameServerSetupResponse.newBuilder();
        builder.setGameId(gameId);
        builder.setSeed(session.seed);
        builder.setMapId(session.mapId);
        for (int i = 0; i < keys.length; i++) {
            builder.addKeys(keys[i]);
        }
        player.getChannel().writeAndFlush(builder.buildPartial());

        //everybody has 10 seconds to connect. it will force start then
        getExecutorService().schedule(() -> {
            checkSessionStart(session, true);
        }, 10000, TimeUnit.MILLISECONDS);
    }

    private void checkSessionStart(GameSession session, boolean forceStart) {
        synchronized (session) {
            if (session.isStarted()) {
                session.sendPlayers();
                return;
            }
            if (session.isDestroyed() || !session.checkStart(forceStart)) {
                return;
            }
            if (session.getPlayers().size() <= 0) {
                session.destroy();
                sessions.remove(session.id);
            } else {
                session.sendPlayers();
                getExecutorService().schedule(() -> {
                    checkSessionLoaded(session, true);
                }, 5000, TimeUnit.MILLISECONDS);
            }
        }
    }

    private void checkSessionLoaded(GameSession session, boolean forceStart) {
        synchronized (session) {
            if (session.isDestroyed() || session.isLoaded() || !session.checkLoaded(forceStart)) {
                return;
            }
            if (session.getPlayers().size() <= 0) {
                session.destroy();
                sessions.remove(session.id);
            } else {
                session.setFuture(gameService.scheduleAtFixedRate(() -> processSession(session), TICK, TICK, TimeUnit.MILLISECONDS));
            }
        }
    }

    private void processSession(GameSession session) {
        try {
            if (session.update()) {
                session.getFuture().cancel(true);
                session.destroy();
                sessions.remove(session.id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public GamePlayer createPlayer(Channel channel) {
        return new GamePlayer(channel);
    }

    @Override
    public void connected(GamePlayer player) {
        System.out.println("Channel connected");
        //TODO add timer to remove player in 10 seconds if they havent send the game id packet.
        //check player count
    }

    @Override
    public void disconnected(GamePlayer player) {
        if (player.getSession() == null)
            return;
        GameSession session = (GameSession) player.getSession();
        session.removePlayer(player);
        if (!session.isStarted() && !session.isLoaded()) {
            session.destroy();
            sessions.remove(session.id);
        }
    }
}
