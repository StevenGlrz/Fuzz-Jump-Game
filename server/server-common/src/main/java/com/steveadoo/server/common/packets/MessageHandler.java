package com.steveadoo.server.common.packets;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;

public class MessageHandler {

    public final int opcode;
    public final Class<? extends GeneratedMessage> messageType;
    public final Decoder decoder;

    public static MessageHandler create(int opcode, final GeneratedMessage message) {
        Decoder decoder = new Decoder() {
            @Override
            public GeneratedMessage decode(byte[] data) {
                try {
                    return message.getParserForType().parseFrom(data);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
        return new MessageHandler(opcode, message.getClass(), decoder);
    }

    private MessageHandler(int opcode, Class<? extends GeneratedMessage> messageType, Decoder decoder) {
        this.opcode = opcode;
        this.messageType = messageType;
        this.decoder = decoder;
    }

    protected interface Decoder<T extends GeneratedMessage> {

        T decode(byte[] data);

    }
}
