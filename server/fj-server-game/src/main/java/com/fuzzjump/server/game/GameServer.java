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

public class GameServer extends FuzzJumpServer<GamePlayer, GameServerInfo> {

    private static final int TICK = 100;

    private final GameServerPlayerValidator gameServerValidator;
    private final ScheduledExecutorService gameService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    private final ConcurrentHashMap<String, GameSession> sessions = new ConcurrentHashMap<>();


    public GameServer(GameServerInfo serverInfo) {
        super(serverInfo, new PacketProcessor(FuzzJumpMessageHandlers.HANDLERS));
        gameServerValidator = new GameServerPlayerValidator(this);
        addValidator(gameServerValidator);
        addValidator(new GameServerMatchmakingValidator(this));
        getPacketProcessor().addListener(Lobby.GameServerSetup.class, this::onGameServerSetup);
        getPacketProcessor().addListener(Game.Loaded.class, this::onGameLoaded);
    }

    private void onGameLoaded(GamePlayer player, Game.Loaded message) {
        if(player.isServer()) {
            player.getChannel().disconnect();
            return;
        }
        try {
            String key = message.getGameId();
            if (!sessions.containsKey(key)) {
                player.getChannel().writeAndFlush(Game.LoadedResponse.newBuilder().setFound(false).buildPartial());
            } else {
                player.getChannel().writeAndFlush(Game.LoadedResponse.newBuilder().setFound(true).buildPartial());
                sessions.get(key).addPlayer(player);
            }
        } catch (Exception e) {
            e.printStackTrace();
            player.getChannel().disconnect();
        }
    }

    private void onGameServerSetup(GamePlayer player, Lobby.GameServerSetup message) {
        if(!player.isServer()) {
            player.getChannel().disconnect();
            return;
        }
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
            builder.addKeys(keys[i]);
        }
        player.getChannel().writeAndFlush(builder.buildPartial());
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
