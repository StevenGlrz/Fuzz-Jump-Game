package com.fuzzjump.server.matchmaking;

import com.steveadoo.server.base.ServerBootstrapper;
import com.steveadoo.server.base.ServerInfo;

import java.net.InetSocketAddress;

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
        try {
            InetSocketAddress gameserverAddress = new InetSocketAddress("10.0.0.5", 6896);
            ServerInfo serverInfo;
            if (args.length > 0 && args[0].equals("local")) {
                serverInfo = new ServerInfo("https://kerpowgamesapi.azurewebsites.net/api/user/validkey", 6895);
                serverInfo.setIp("10.0.0.5");
            } else {
                serverInfo = new ServerInfo("https://kerpowgamesapi.azurewebsites.net/api/user/validkey", 6894, 6895);
            }
            ServerBootstrapper.bootstrap(new MatchmakingServer(serverInfo, gameserverAddress));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error starting server");
        }
    }

}
