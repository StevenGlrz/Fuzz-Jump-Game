package com.fuzzjump.server.matchmaking;

import com.fuzzjump.server.common.FuzzJumpMessageHandlers;
import com.fuzzjump.server.common.messages.lobby.Lobby;
import com.fuzzjump.server.matchmaking.lobby.LobbySession;
import com.steveadoo.server.base.ConnectionValidator;
import com.steveadoo.server.base.Player;
import com.steveadoo.server.base.net.KerpowGameDecoder;
import com.steveadoo.server.base.net.KerpowGameEncoder;
import com.steveadoo.server.common.Join;
import com.steveadoo.server.common.packets.Packet;
import com.steveadoo.server.common.packets.PacketHandler;
import com.steveadoo.server.common.packets.Validation;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameServerTransferer {

    private static final AttributeKey<LobbySession> SESSION_ATTRIBUTE_KEY = AttributeKey.newInstance("GameServerTransfer.session");
    private static final AttributeKey<String> SERVER_IP_KEY = AttributeKey.newInstance("GameServerTransfer.ip");
    private static final AttributeKey<Integer> SERVER_PORT_KEY = AttributeKey.newInstance("GameServerTransfer.port");

    private final InetSocketAddress gameServerAddress;
    private final PacketHandler packetHandler;
    private final ExecutorService connectionExecutor = Executors.newSingleThreadExecutor();

    private Bootstrap bootstrap;

    public GameServerTransferer(InetSocketAddress gameServerAddress) {
        this.gameServerAddress = gameServerAddress;
        this.packetHandler = new PacketHandler(FuzzJumpMessageHandlers.handlers);
        packetHandler.addListener(Join.JoinResponsePacket.class, this::onJoinResponse);
        packetHandler.addListener(Lobby.GameServerSetupResponse.class, this::onServerSetupResponse);
        init();
    }

    private void onServerSetupResponse(Channel channel, Lobby.GameServerSetupResponse message) {
        LobbySession session = channel.attr(SESSION_ATTRIBUTE_KEY).get();
        String serverIp = channel.attr(SERVER_IP_KEY).get();
        int port = channel.attr(SERVER_PORT_KEY).get();
        Lobby.GameServerSetupData.Builder builder = Lobby.GameServerSetupData.newBuilder();
        builder.setGameId(message.getGameId());
        builder.setSeed(message.getSeed());
        builder.setMapId(message.getMapId());
        builder.setIp(serverIp);
        builder.setPort(port);
        for(int i = 0; i < session.getPlayers().size(); i++) {
            Player player = session.getPlayers().get(i);
            player.channel.writeAndFlush(builder.setKey(message.getKeys(i)).buildPartial());
            player.channel.disconnect();
        }
        channel.attr(SESSION_ATTRIBUTE_KEY).set(null);
        channel.disconnect();
    }

    private void onJoinResponse(Channel channel, Join.JoinResponsePacket message) {
        LobbySession session = channel.attr(SESSION_ATTRIBUTE_KEY).get();
        Lobby.GameServerFound serverFoundMessage;
        if (message.getStatus() == Validation.AUTHORIZED) {
            serverFoundMessage = Lobby.GameServerFound.newBuilder().setFound(true).build();
            channel.attr(SERVER_IP_KEY).set(message.getServerIp());
            channel.attr(SERVER_PORT_KEY).set(message.getServerPort());
            channel.writeAndFlush(Lobby.GameServerSetup.newBuilder().setMapId(session.getWinningMapId()).setPlayerCount(session.getPlayers().size()));
        } else {
            serverFoundMessage = Lobby.GameServerFound.newBuilder().setFound(false).build();
        }
        for(Player player : session.getPlayers()) {
            player.channel.writeAndFlush(serverFoundMessage);
        }
    }

    private void init() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new KerpowGameDecoder());
                pipeline.addLast(new KerpowGameEncoder(packetHandler));
                pipeline.addLast(new ClientHandler(GameServerTransferer.this));
            }
        });
    }

    public void transfer(LobbySession session) {
        ChannelFuture future = bootstrap.connect(gameServerAddress);
        future.addListener(f -> {
            if (f.isSuccess()) {
                future.channel().attr(SESSION_ATTRIBUTE_KEY).set(session);
                sendJoinPacket(future.channel());
            } else {
                for (Player player : session.getPlayers()) {
                    player.channel.disconnect();
                }
            }
        });
        session.setListener(() -> {
            if (future.channel() != null) {
                future.channel().disconnect();
            }
        });
    }

    private void sendJoinPacket(Channel channel) {
        Join.JoinPacket.Builder builder = Join.JoinPacket.newBuilder();
        builder.setProfileId(-1);
        builder.setVersion(1);
        builder.setServerSessionKey(ConnectionValidator.MASTER_KEY);
        channel.writeAndFlush(builder.buildPartial());
    }

    public class ClientHandler extends ChannelInboundHandlerAdapter {

        private final GameServerTransferer transferer;

        public ClientHandler(GameServerTransferer transferer) {
            this.transferer = transferer;
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) {
            disconnectPlayers(ctx.channel());
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            Packet packet = (Packet) msg;
            transferer.packetHandler.packetReceived(ctx.channel(), packet);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) {
            throwable.printStackTrace();
            disconnectPlayers(ctx.channel());
        }

        public void disconnectPlayers(Channel channel) {
            Attribute<LobbySession> attrib = channel.attr(SESSION_ATTRIBUTE_KEY);
            if (attrib == null)
                return;
            LobbySession session = attrib.get();
            if (session == null)
                return;
            Lobby.GameServerFound serverFoundMessage = Lobby.GameServerFound.newBuilder().setFound(false).build();
            for(Player player : session.getPlayers()) {
                player.channel.writeAndFlush(serverFoundMessage);
                player.channel.disconnect();
            }
        }

    }
}
