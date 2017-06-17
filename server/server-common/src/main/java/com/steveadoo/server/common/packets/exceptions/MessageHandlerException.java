package com.steveadoo.server.common.packets.exceptions;

public class MessageHandlerException extends Throwable {

    public MessageHandlerException(String message, Object identifier) {
        super(message + " for packet: " + identifier);
    }

}
