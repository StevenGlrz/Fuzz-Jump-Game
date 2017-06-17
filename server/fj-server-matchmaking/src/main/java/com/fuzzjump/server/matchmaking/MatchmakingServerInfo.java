package com.fuzzjump.server.matchmaking;

import com.fuzzjump.server.base.FuzzJumpServerInfo;

/**
 * Created by Steveadoo on 6/16/2017.
 */

public class MatchmakingServerInfo extends FuzzJumpServerInfo {

    private final String gameServerIp;

    public MatchmakingServerInfo(int port,
                                 int privatePort,
                                 String directIp,
                                 String gameServerIp) {
        super(port, privatePort, directIp);
        this.gameServerIp = gameServerIp;
    }

}
