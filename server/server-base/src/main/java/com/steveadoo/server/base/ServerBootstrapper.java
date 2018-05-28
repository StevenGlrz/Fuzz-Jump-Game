package com.steveadoo.server.base;

import com.steveadoo.server.base.net.GamePacketDecoder;
import com.steveadoo.server.base.net.GamePacketEncoder;
import com.steveadoo.server.base.net.GameServerHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ServerBootstrapper {

    public ServerBootstrapper() {

    }

    public ServerBootstrap bootstrap(final Server server) {
        ServerConfig serverConfig = server.getServerInfo();
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {

                    @Override
                    protected void initChannel(Channel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new GamePacketDecoder());
                        pipeline.addLast(new GamePacketEncoder(server));
                        pipeline.addLast(new GameServerHandler(server));
                    }

                })
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true);
        try {
            bootstrap.bind(new InetSocketAddress(serverConfig.port)).get();
            System.out.println("Listening on port " + serverConfig.port);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return bootstrap;
    }


}
