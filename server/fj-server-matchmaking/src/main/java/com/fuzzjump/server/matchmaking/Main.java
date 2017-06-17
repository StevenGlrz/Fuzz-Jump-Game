package com.fuzzjump.server.matchmaking;

import com.steveadoo.server.base.ServerBootstrapper;
import com.steveadoo.server.base.ServerInfo;

import java.net.InetSocketAddress;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

import io.netty.bootstrap.ServerBootstrap;

/**
 * Created by Steveadoo on 12/13/2015.
 */
public class Main {

    /*private static final ServerInfo SERVER_INFO = new ServerInfo("https://management.core.windows.net",
            "9b59165c-2559-49e3-82d2-096d1c2ab432",
            "fj-azure-keystore.jks",
            "gRvbH43LPoNXdERrT32j",
            "LBPROBE",
            "https://kerpowgamesapi.azurewebsites.net/api/user/validkey",
            5695,
            6894,
            6895);*/

    public static void main(String[] args) {
        MatchmakingServerInfo serverInfo = loadServerInfo(args);
        MatchmakingServer server = new MatchmakingServer(serverInfo);
        ServerBootstrapper bootstrapper = new ServerBootstrapper();
        ServerBootstrap bootstrap = bootstrapper.bootstrap(server);
        bootstrap.bind(new InetSocketAddress(serverInfo.privatePort));
    }

    private static MatchmakingServerInfo loadServerInfo(String[] args) {
        return new MatchmakingServerInfo(
                Integer.parseInt(args[0]),
                Integer.parseInt(args[1]),
                args[2],
                args[3]
        );
    }

}
