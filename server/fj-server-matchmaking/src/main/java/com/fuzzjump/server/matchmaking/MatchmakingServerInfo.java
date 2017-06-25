package com.fuzzjump.server.matchmaking;

import com.fuzzjump.server.base.FuzzJumpServerInfo;

public class MatchmakingServerInfo extends FuzzJumpServerInfo {

    public final String gameServerIp;
    public final int gameServerPort;

    public MatchmakingServerInfo(FuzzJumpServerInfo info, String gameServerIp, int gameServerPort) {
        super(info);
        this.gameServerIp = gameServerIp;
        this.gameServerPort = gameServerPort;
    }

}
