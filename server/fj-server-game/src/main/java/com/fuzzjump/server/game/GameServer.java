package com.fuzzjump.server.game;

import com.fuzzjump.server.common.FuzzJumpMessageHandlers;
import com.fuzzjump.server.common.messages.game.Game;
import com.fuzzjump.server.common.messages.lobby.Lobby;
import com.fuzzjump.server.game.game.GamePlayer;
import com.fuzzjump.server.game.game.GameSession;
import com.steveadoo.server.base.Player;
import com.steveadoo.server.base.Server;
import com.steveadoo.server.base.ServerInfo;
import com.steveadoo.server.common.packets.PacketHandler;

import io.netty.channel.Channel;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.*;

public class GameServer extends Server {

    private static final int TICK = 100;

    private ExecutorService gameService = Executors.newCachedThreadPool();

    private ConcurrentHashMap<String, GameSession> sessions = new ConcurrentHashMap<>();


    public GameServer(ServerInfo serverInfo) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        super(serverInfo, new PacketHandler(FuzzJumpMessageHandlers.handlers), true);
        getPacketHandler().addListener(Lobby.GameServerSetup.class, this::onGameServerSetup);
        getPacketHandler().addListener(Game.Loaded.class, this::onGameLoaded);
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
        try {
            String[] keys = validator.generateKeys(keyCount);
            String gameId = UUID.randomUUID().toString();
            GameSession session = new GameSession(message.getMapId(), gameId, message.getPlayerCount());
            sessions.put(gameId, session);
            gameService.submit(() -> processSession(session));
            Lobby.GameServerSetupResponse.Builder builder = Lobby.GameServerSetupResponse.newBuilder();
            builder.setGameId(gameId);
            builder.setSeed(session.seed);
            builder.setMapId(session.mapId);
            for(int i = 0; i < keys.length; i++) {
                builder.setKeys(i, keys[i]);
            }
            channel.writeAndFlush(builder.buildPartial());
        } catch (UnsupportedEncodingException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            channel.disconnect();
        }
    }

    private void processSession(GameSession session) {
        while(session.update()) {
            try {
                Thread.sleep(TICK);
            } catch (InterruptedException e) {
                e.printStackTrace();
                session.destroy();
                break;
            }
        }
        sessions.remove(session.id);
    }

    @Override
    public Player createPlayer(Channel channel, long profileId) {
        return new GamePlayer(channel, profileId);
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
    }
}
