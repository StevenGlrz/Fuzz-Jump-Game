package com.fuzzjump.game.model.profile;

import com.fuzzjump.game.game.player.Appearance;
import com.kerpowgames.fuzzjump.common.Game;
import com.kerpowgames.fuzzjump.common.Lobby;

/**
 * Created by Steveadoo on 12/8/2015.
 */
public class PlayerProfile extends Profile {

    public static final int MAX_RANK = 100;

    private boolean ready;

    public PlayerProfile() {
    }

    public PlayerProfile(Lobby.Player player) {
        this();
        this.profileId = player.getProfileId();
        this.ready = player.getReady();
        this.playerIndex = player.getPlayerIndex();
        this.name = "Waiting";
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
        raiseEvent();
    }
}
