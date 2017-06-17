package com.fuzzjump.server.game;

import com.fuzzjump.server.base.FuzzJumpServer;
import com.fuzzjump.server.common.FuzzJumpMessageHandlers;
import com.fuzzjump.server.common.messages.game.Game;
import com.fuzzjump.server.common.messages.lobby.Lobby;
import com.fuzzjump.server.game.game.GamePlayer;
import com.fuzzjump.server.game.game.GameSession;
import com.steveadoo.server.common.packets.PacketProcessor;

import io.netty.channel.Channel;

import java.util.UUID;
import java.util.concurrent.*;

public class GameServer extends FuzzJumpServer<GamePlayer, GameServerInfo> {

    private static final int TICK = 100;

    private final GameServerValidator gameServerValidator;
    private final ScheduledExecutorService gameService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    private final ConcurrentHashMap<String, GameSession> sessions = new ConcurrentHashMap<>();


    public GameServer(GameServerInfo serverInfo) {
        super(serverInfo, new PacketProcessor(FuzzJumpMessageHandlers.HANDLERS));
        gameServerValidator = new GameServerValidator(this);
        addValidator(gameServerValidator);
        getPacketProcessor().addListener(Lobby.GameServerSetup.class, this::onGameServerSetup);
        getPacketProcessor().addListener(Game.Loaded.class, this::onGameLoaded);
    }

    private void onGameLoaded(GamePlayer player, Game.Loaded message) {
        try {
            String key = message.getGameId();
            if (!sessions.containsKey(key)) {
                player.channel.writeAndFlush(Game.LoadedResponse.newBuilder().setFound(false).buildPartial());
            } else {
                player.channel.writeAndFlush(Game.LoadedResponse.newBuilder().setFound(true).buildPartial());
                sessions.get(key).addPlayer(player);
            }
        } catch (Exception e) {
            e.printStackTrace();
            player.channel.disconnect();
        }
    }

    private void onGameServerSetup(Channel channel, Lobby.GameServerSetup message) {
        int keyCount = message.getPlayerCount();
        String[] keys = gameServerValidator.generateSessionKeys(keyCount);
        String gameId = UUID.randomUUID().toString();
        GameSession session = new GameSession(message.getMapId(), gameId, message.getPlayerCount());
        sessions.put(gameId, session);
        session.setTickFuture(gameService.scheduleAtFixedRate(() -> processSession(session), 0, TICK, TimeUnit.MILLISECONDS));
        Lobby.GameServerSetupResponse.Builder builder = Lobby.GameServerSetupResponse.newBuilder();
        builder.setGameId(gameId);
        builder.setSeed(session.seed);
        builder.setMapId(session.mapId);
        for (int i = 0; i < keys.length; i++) {
            builder.setKeys(i, keys[i]);
        }
        channel.writeAndFlush(builder.buildPartial());
    }

    private void processSession(GameSession session) {
        if (!session.update()) {
            session.getTickFuture().cancel(true);
            session.destroy();
            sessions.remove(session.id);
        }
    }

    @Override
    public GamePlayer createPlayer(Channel channel) {
        return new GamePlayer(channel);
    }

    @Override
    public void connected(GamePlayer player) {
        System.out.println("Channel connected");
    }

    @Override
    public void disconnected(GamePlayer player) {
        if (player.getSession() == null)
            return;
        player.getSession().removePlayer(player);
    }
}
