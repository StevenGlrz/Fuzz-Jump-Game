package com.fuzzjump.server.common;

public class FuzzJumpPackets {

    public static final int JOIN_PACKET = 0;
    public static final int JOIN_PACKET_RESPONSE = 1;
    public static final int JOIN_SERVER_PACKET = 9;

    public static final int LOBBY_STATE = 2;
    public static final int TIME_STATE_UPDATE = 3;
    public static final int MAP_SLOT_VOTES_UPDATE = 4;
    public static final int MAP_SLOT_UPDATE = 5;
    public static final int READY_UPDATE = 6;
    public static final int LOBBY_LOADED = 7;

    public static final int GAME_FOUND = 8;
    public static final int GAME_SERVER_FOUND = 10;
    public static final int GAME_SERVER_SETUP = 11;
    public static final int GAME_SERVER_SETUP_RESPONSE = 12;
    public static final int GAME_SERVER_SETUP_DATA = 13;

    public static final int GAME_COUNTDOWN = 14;
    public static final int GAME_JOIN = 15;
    public static final int GAME_JOIN_RESPONSE = 16;
    public static final int GAME_LOADED = 17;
    public static final int GAME_READY = 18;

}
