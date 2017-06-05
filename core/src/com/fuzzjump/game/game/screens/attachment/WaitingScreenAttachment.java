package com.fuzzjump.game.game.screens.attachment;

import com.fuzzjump.game.net.GameSession;

/**
 * Created by Steveadoo on 12/29/2015.
 */
public class WaitingScreenAttachment implements ScreenAttachment {

    private final GameSession session;

    public WaitingScreenAttachment(GameSession session) {
        this.session = session;
    }

    public GameSession getSession() {
        return session;
    }
}
