package com.steveadoo.server.base;

import com.steveadoo.server.base.net.KerpowGameDecoder;
import com.steveadoo.server.base.net.KerpowGameEncoder;
import com.steveadoo.server.base.net.KerpowServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class ServerBootstrapper {


    public static ServerBootstrap bootstrap(Server server) {
        ServerInfo serverInfo = server.getServerInfo();
        try {
            if (serverInfo.getLbPort() != -1) {
                System.out.println("Finding our ip...");
                serverInfo.setIp(findIp());
                System.out.println("Our ip: " + serverInfo.getIp());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {

                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new KerpowGameDecoder());
                        pipeline.addLast(new KerpowGameEncoder(server.getPacketHandler()));
                        pipeline.addLast(new KerpowServerHandler(server));
                    }

                })
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true);

        System.out.println("Listening on " + serverInfo.getPrivatePort());
        bootstrap.bind(new InetSocketAddress(serverInfo.getPrivatePort()));
        if (serverInfo.getLbPort() != -1) {
            try {
                server.setServerStateListener(new LbServerStateListener(bootstrap, serverInfo.getLbPort()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return bootstrap;
    }

    private static String findIp() throws IOException {
        URLConnection connection = new URL("http://169.254.169.254/latest/meta-data/public-ipv4").openConnection();
        connection.connect();
        HttpURLConnection httpConnection = (HttpURLConnection) connection;
        BufferedReader rd = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
        String line = rd.readLine();
        rd.close();
        return line;
    }

}
