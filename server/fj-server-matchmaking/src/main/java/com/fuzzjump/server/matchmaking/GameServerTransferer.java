package com.fuzzjump.server.matchmaking;

import com.fuzzjump.server.base.FuzzJumpPlayer;
import com.fuzzjump.server.common.FuzzJumpMessageHandlers;
import com.fuzzjump.server.common.messages.join.Join;
import com.fuzzjump.server.common.messages.lobby.Lobby;
import com.fuzzjump.server.matchmaking.lobby.LobbyPlayer;
import com.fuzzjump.server.matchmaking.lobby.LobbySession;
import com.steveadoo.server.base.net.GamePacketDecoder;
import com.steveadoo.server.base.net.GamePacketEncoder;
import com.steveadoo.server.common.packets.Packet;
import com.steveadoo.server.common.packets.PacketProcessor;
import com.steveadoo.server.common.packets.Validation;
import com.steveadoo.server.common.packets.exceptions.MessageHandlerException;
import com.steveadoo.server.common.packets.exceptions.MissingHandlerException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class GameServerTransferer {

    private static final AttributeKey<LobbySession> SESSION_ATTRIBUTE_KEY = AttributeKey.newInstance("GameServerTransfer.session");
    private static final AttributeKey<String> SERVER_IP_KEY = AttributeKey.newInstance("GameServerTransfer.ip");
    private static final AttributeKey<Integer> SERVER_PORT_KEY = AttributeKey.newInstance("GameServerTransfer.port");

    private final MatchmakingServer matchmakingServer;
    private final PacketProcessor packetProcessor;

    private String machineName;

    private Bootstrap bootstrap;

    public GameServerTransferer(MatchmakingServer matchmakingServer) {
        this.matchmakingServer = matchmakingServer;
        this.packetProcessor = new PacketProcessor(FuzzJumpMessageHandlers.HANDLERS);
        this.init();
    }

    private void init() {

        try {
            this.machineName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot find machine name");
        }

        EventLoopGroup workerGroup = new NioEventLoopGroup();

        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new GamePacketDecoder());
                pipeline.addLast(new GamePacketEncoder(matchmakingServer.getPacketProcessor()));
                pipeline.addLast(new ClientHandler(GameServerTransferer.this));
            }
        });

        packetProcessor.addListener(Join.JoinResponsePacket.class, this::joinResponse);
        packetProcessor.addListener(Lobby.GameServerSetupResponse.class, this::onServerSetupResponse);
    }

    public void transfer(LobbySession session) {
        connect(5).thenAccept(channel -> {
            if (channel == null) {
                Lobby.GameServerFound serverFoundMessage = Lobby.GameServerFound.newBuilder().setFound(false).build();
                for(FuzzJumpPlayer player : session.getPlayers()) {
                    player.getChannel().writeAndFlush(serverFoundMessage);
                    player.getChannel().disconnect();
                }
                return;
            }
            channel.attr(SESSION_ATTRIBUTE_KEY).set(session);
            sendJoinPacket(channel);
            session.setListener(channel::disconnect);
        });
    }

    private CompletableFuture<Channel> connect(int retryCount) {
        CompletableFuture<Channel> channelFuture = new CompletableFuture<>();
        ChannelFuture future = bootstrap.connect(matchmakingServer.getServerInfo().gameServerIp, matchmakingServer.getServerInfo().gameServerPort);
        int[] retries = new int[1];
        future.addListener(f -> {
            if (!f.isSuccess()) {
                if (retries[0] >= retryCount) {
                    channelFuture.complete(null);
                }
                retries[0]++;
                return;
            }
            channelFuture.complete(future.channel());
        });
        return channelFuture;
    }

    private void sendJoinPacket(Channel channel) {
        matchmakingServer.getApi().getSessionService().getServerSessionToken(machineName, "MATCH->GAME").subscribe(response -> {
            try {
                Join.JoinServerPacket.Builder builder = Join.JoinServerPacket.newBuilder();
                builder.setVersion(1);
                builder.setSessionKey(response.getBody());
                builder.setMachineName(machineName);
                channel.writeAndFlush(builder.buildPartial());
            } catch (Exception e) {
                e.printStackTrace();
                //TODO retry count here.
                matchmakingServer.getExecutorService().schedule(() -> sendJoinPacket(channel), 5000, TimeUnit.MILLISECONDS);
            }
        }, err -> matchmakingServer.getExecutorService().schedule(() -> sendJoinPacket(channel), 1000, TimeUnit.MILLISECONDS));
    }

    private void joinResponse(Channel channel, Join.JoinResponsePacket message) {
        LobbySession session = channel.attr(SESSION_ATTRIBUTE_KEY).get();
        Lobby.GameServerFound serverFoundMessage;
        if (message.getStatus() == Validation.AUTHORIZED) {
            serverFoundMessage = Lobby.GameServerFound.newBuilder().setFound(true).buildPartial();
            channel.attr(SERVER_IP_KEY).set(message.getServerIp());
            channel.attr(SERVER_PORT_KEY).set(message.getServerPort());
            channel.writeAndFlush(Lobby.GameServerSetup.newBuilder().setMapId(session.getWinningMapId()).setPlayerCount(session.getPlayers().size()).buildPartial());
        } else {
            serverFoundMessage = Lobby.GameServerFound.newBuilder().setFound(false).buildPartial();
        }
        for(LobbyPlayer player : session.getPlayers()) {
            player.getChannel().writeAndFlush(serverFoundMessage);
        }
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
            LobbyPlayer player = session.getPlayers().get(i);
            player.getChannel().writeAndFlush(builder.setKey(message.getKeys(i)).buildPartial());
        }
        channel.attr(SESSION_ATTRIBUTE_KEY).set(null);
        channel.disconnect();
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
            try {
                transferer.packetProcessor.processPacket(ctx.channel(), packet);
            } catch (MissingHandlerException | MessageHandlerException e) {
                e.printStackTrace();
            }
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
            //if the session is null, then disconnect in 5 seconds. it means we found a server
            if (session == null) {
                matchmakingServer.getExecutorService().schedule(() -> {
                    for(FuzzJumpPlayer player : session.getPlayers()) {
                        if (player.getChannel().isOpen()) {
                            player.getChannel().disconnect();
                        }
                    }
                }, 5000, TimeUnit.MILLISECONDS);
                return;
            }
            Lobby.GameServerFound serverFoundMessage = Lobby.GameServerFound.newBuilder().setFound(false).build();
            for(FuzzJumpPlayer player : session.getPlayers()) {
                player.getChannel().writeAndFlush(serverFoundMessage);
            }
            matchmakingServer.getExecutorService().schedule(() -> {
                for(FuzzJumpPlayer player : session.getPlayers()) {
                    if (player.getChannel().isOpen()) {
                        player.getChannel().disconnect();
                    }
                }
            }, 5000, TimeUnit.MILLISECONDS);
        }

    }

}
