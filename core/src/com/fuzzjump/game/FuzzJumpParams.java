package com.fuzzjump.game;

public class FuzzJumpParams {

    public final String apiUrl;
    public final String matchmakingIp;
    public final int matchmakingPort;

    public FuzzJumpParams(String apiUrl, String matchmakingIp, int matchmakingPort) {
        this.apiUrl = apiUrl;
        this.matchmakingIp = matchmakingIp;
        this.matchmakingPort = matchmakingPort;
    }

}
