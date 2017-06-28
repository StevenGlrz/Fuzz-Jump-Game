package com.fuzzjump.server.game;

import com.fuzzjump.server.base.FuzzJumpServerConfig;
import com.steveadoo.server.base.ServerBootstrapper;

import java.io.IOException;
import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;

public class Main {

    public static void main(String[] args) throws IOException {
        GameServerConfig config = loadConfig(args);
        GameServer server = new GameServer(config);
        ServerBootstrapper bootstrapper = new ServerBootstrapper();
        ServerBootstrap bootstrap = bootstrapper.bootstrap(server);
        if (config.port != config.privatePort) {
            bootstrap.bind(new InetSocketAddress(config.privatePort));
            System.out.println("Listening on port " + config.privatePort);
        }
    }

    private static GameServerConfig loadConfig(String[] args) throws IOException {
        return new GameServerConfig(FuzzJumpServerConfig.loadConfig(args));
    }

}
