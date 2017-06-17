package com.steveadoo.server.base;

import com.steveadoo.server.base.net.GamePacketDecoder;
import com.steveadoo.server.base.net.GamePacketEncoder;
import com.steveadoo.server.base.net.GameServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.*;

public class ServerBootstrapper {

    public ServerBootstrapper() {

    }

    public ServerBootstrap bootstrap(final Server server) {
        ServerInfo serverInfo = server.getServerInfo();
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {

                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new GamePacketDecoder());
                        pipeline.addLast(new GamePacketEncoder(server));
                        pipeline.addLast(new GameServerHandler(server));
                    }

                })
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true);

        System.out.println("Listening on " + serverInfo.port);
        bootstrap.bind(new InetSocketAddress(serverInfo.port));
        return bootstrap;
    }


}
