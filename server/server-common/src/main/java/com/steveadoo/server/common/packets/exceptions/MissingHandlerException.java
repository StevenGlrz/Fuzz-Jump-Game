package com.steveadoo.server.common.packets.exceptions;

public class MissingHandlerException extends Throwable {

    public MissingHandlerException(Object identifier) {
        super("No handler for identifier: " + identifier);
    }
}
