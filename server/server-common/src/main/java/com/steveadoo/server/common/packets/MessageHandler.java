package com.steveadoo.server.common.packets;

/**
 * Handles encoding/decoding messages
 * @param <T> The class type to encode from and decode to
 */
public class MessageHandler<T> {

    public final int opcode;
    public final Class<T> messageType;
    public final Decoder<T> decoder;
    public final Encoder<T> encoder;

    public MessageHandler(Class<T> messageType, int opcode, Decoder<T> decoder, Encoder<T> encoder) {
        this.opcode = opcode;
        this.messageType = messageType;
        this.decoder = decoder;
        this.encoder = encoder;
    }

    public interface Decoder<T> {

        /**
         * Decode the body of the message(so dont look for the id)
         * @param data the message to decode
         * @return the decoded message
         */
        T decode(byte[] data);

    }

    public interface Encoder<T> {

        /**
         * Encode the body of the message(so dont add the id)
         * @param message the message to encode
         * @return the encoded message
         */
        byte[] encode(T message);

    }
}
