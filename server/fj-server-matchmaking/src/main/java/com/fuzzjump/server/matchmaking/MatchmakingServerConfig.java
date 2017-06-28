package com.fuzzjump.server.matchmaking;

import com.fuzzjump.server.base.FuzzJumpServerConfig;

public class MatchmakingServerConfig extends FuzzJumpServerConfig {

    public final String gameServerIp;
    public final int gameServerPort;

    public MatchmakingServerConfig(FuzzJumpServerConfig config, String gameServerIp, int gameServerPort) {
        super(config);
        this.gameServerIp = gameServerIp;
        this.gameServerPort = gameServerPort;
    }

}
