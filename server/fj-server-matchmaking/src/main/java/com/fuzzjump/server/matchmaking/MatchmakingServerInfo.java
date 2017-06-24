package com.fuzzjump.server.matchmaking;

import com.fuzzjump.server.base.FuzzJumpServerInfo;

public class MatchmakingServerInfo extends FuzzJumpServerInfo {

    private final String gameServerIp;

    public MatchmakingServerInfo(FuzzJumpServerInfo info, String gameServerIp) {
        super(info);
        this.gameServerIp = gameServerIp;
    }

}
