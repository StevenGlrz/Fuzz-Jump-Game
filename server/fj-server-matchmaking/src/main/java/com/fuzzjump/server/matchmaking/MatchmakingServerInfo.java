package com.fuzzjump.server.matchmaking;

import com.fuzzjump.server.base.FuzzJumpServerInfo;

public class MatchmakingServerInfo extends FuzzJumpServerInfo {

    private final String gameServerIp;
    private final String gameServerPort;

    public MatchmakingServerInfo(FuzzJumpServerInfo info, String gameServerIp, String gameServerPort) {
        super(info);
        this.gameServerIp = gameServerIp;
        this.gameServerPort = gameServerPort;
    }

}
