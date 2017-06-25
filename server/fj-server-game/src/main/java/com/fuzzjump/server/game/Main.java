package com.fuzzjump.server.game;

import com.fuzzjump.server.base.FuzzJumpServerInfo;
import com.steveadoo.server.base.ServerBootstrapper;
import com.steveadoo.server.base.ServerInfo;

import java.io.IOException;
import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;

public class Main {

    public static void main(String[] args) throws IOException {
        GameServerInfo serverInfo = loadServerInfo(args);
        GameServer server = new GameServer(serverInfo);
        ServerBootstrapper bootstrapper = new ServerBootstrapper();
        ServerBootstrap bootstrap = bootstrapper.bootstrap(server);
        if (serverInfo.port != serverInfo.privatePort) {
            bootstrap.bind(new InetSocketAddress(serverInfo.privatePort));
            System.out.println("Listening on port " + serverInfo.privatePort);
        }
    }

    private static GameServerInfo loadServerInfo(String[] args) throws IOException {
        return new GameServerInfo(FuzzJumpServerInfo.loadBaseInfo(args));
    }

}
