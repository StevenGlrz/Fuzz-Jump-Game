package com.fuzzjump.server.base;

import com.steveadoo.server.base.ServerInfo;

public class FuzzJumpServerInfo extends ServerInfo {

    //the port that players use for direct connection
    public final int privatePort;
    //the ip of this machine
    public final String ip;

    public FuzzJumpServerInfo(int port, int privatePort, String directIp) {
        super(port);
        this.privatePort = privatePort;
        this.ip = directIp;
    }

}
