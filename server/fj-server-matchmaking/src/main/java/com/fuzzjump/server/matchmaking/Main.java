package com.fuzzjump.server.matchmaking;

import com.fuzzjump.server.base.FuzzJumpServerInfo;
import com.steveadoo.server.base.ServerBootstrapper;
import com.steveadoo.server.base.ServerInfo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

import io.netty.bootstrap.ServerBootstrap;

public class Main {

    public static void main(String[] args) throws IOException {
        MatchmakingServerInfo serverInfo = loadServerInfo(args);
        MatchmakingServer server = new MatchmakingServer(serverInfo);
        ServerBootstrapper bootstrapper = new ServerBootstrapper();
        ServerBootstrap bootstrap = bootstrapper.bootstrap(server);
        if (serverInfo.port != serverInfo.privatePort) {
            bootstrap.bind(new InetSocketAddress(serverInfo.privatePort));
            System.out.println("Listening on port " + serverInfo.privatePort);
        }
    }

    private static MatchmakingServerInfo loadServerInfo(String[] args) throws IOException {
        String gameServerIp = System.getenv("FUZZ_MATCHMAKING_GAMESERVER_IP");
        String gameServerPort = System.getenv("FUZZ_MATCHMAKING_GAMESERVER_PORT");
        return new MatchmakingServerInfo(FuzzJumpServerInfo.loadBaseInfo(args), gameServerIp, gameServerPort);
    }

}
