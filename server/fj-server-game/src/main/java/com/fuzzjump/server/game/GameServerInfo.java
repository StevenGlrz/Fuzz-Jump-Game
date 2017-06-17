package com.fuzzjump.server.game;

import com.fuzzjump.server.base.FuzzJumpServerInfo;

public class GameServerInfo extends FuzzJumpServerInfo {

    public GameServerInfo(int port,
                          int privatePort,
                          String directIp) {
        super(port, privatePort, directIp);
    }

}
