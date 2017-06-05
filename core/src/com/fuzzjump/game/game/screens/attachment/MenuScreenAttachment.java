package com.fuzzjump.game.game.screens.attachment;

/**
 * Created by Steven Galarza on 8/18/2015.
 */
public class MenuScreenAttachment implements ScreenAttachment {

    private boolean authenticate;
    private boolean error;

    public MenuScreenAttachment(boolean authenticate, boolean error) {
        this.authenticate = authenticate;
        this.error = error;
    }

    public boolean isAuthenticate() {
        return authenticate;
    }

    public boolean isError() {
        return error;
    }
}
