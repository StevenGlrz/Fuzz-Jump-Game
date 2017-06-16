package com.fuzzjump.server.matchmaking.lobby;

import com.steveadoo.server.base.Player;
import io.netty.channel.Channel;

public class LobbyPlayer extends Player {

    public boolean ready;
    public int mapId = -1;

    public LobbyPlayer(Channel channel, long profileId) {
        super(channel, profileId);
    }
}
