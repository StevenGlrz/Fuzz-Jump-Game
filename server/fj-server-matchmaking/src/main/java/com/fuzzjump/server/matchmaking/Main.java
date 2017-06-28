package com.fuzzjump.server.matchmaking;

import com.fuzzjump.server.base.FuzzJumpServerConfig;
import com.steveadoo.server.base.ServerBootstrapper;

import java.io.IOException;
import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;

public class Main {

    public static void main(String[] args) throws IOException {
        MatchmakingServerConfig serverConfig = loadServerInfo(args);
        MatchmakingServer server = new MatchmakingServer(serverConfig);
        ServerBootstrapper bootstrapper = new ServerBootstrapper();
        ServerBootstrap bootstrap = bootstrapper.bootstrap(server);
        if (serverConfig.port != serverConfig.privatePort) {
            bootstrap.bind(new InetSocketAddress(serverConfig.privatePort));
            System.out.println("Listening on port " + serverConfig.privatePort);
        }
    }

    private static MatchmakingServerConfig loadServerInfo(String[] args) throws IOException {
        String gameServerIp = System.getenv("FUZZ_MATCHMAKING_GAMESERVER_IP");
        String gameServerPort = System.getenv("FUZZ_MATCHMAKING_GAMESERVER_PORT");
        return new MatchmakingServerConfig(FuzzJumpServerConfig.loadConfig(args), gameServerIp, Integer.parseInt(gameServerPort));
    }

}
