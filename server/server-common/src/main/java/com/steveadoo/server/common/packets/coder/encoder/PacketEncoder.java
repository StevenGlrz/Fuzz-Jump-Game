package com.steveadoo.server.common.packets.coder.encoder;

import com.steveadoo.server.common.packets.MessageHandler;
import com.steveadoo.server.common.packets.Packet;
import com.steveadoo.server.common.packets.coder.Coder;
import com.steveadoo.server.common.packets.coder.HandlerStorage;
import com.steveadoo.server.common.packets.exceptions.MessageHandlerException;
import com.steveadoo.server.common.packets.exceptions.MissingHandlerException;

import java.util.List;

public class PacketEncoder extends Coder<Class<?>, MessageHandler> {

    public PacketEncoder(HandlerStorage<Class<?>, MessageHandler> storage, List<MessageHandler> handlers) {
        super(storage);
        init(handlers);
    }

    public PacketEncoder(List<MessageHandler> handlers) {
        this(Coder.<Class<?>, MessageHandler>getStorageForSize(handlers.size()), handlers);
    }

    private void init(List<MessageHandler> handlers) {
        for(MessageHandler handler : handlers) {
            addHandler(handler.messageType, handler);
        }
    }

    public Packet encodePacket(Object message) throws MissingHandlerException, MessageHandlerException {
        MessageHandler handler = getHandler(message.getClass());
        if (handler == null) {
            throw new MissingHandlerException(message.getClass());
        }
        if (handler.decoder == null) {
            throw new MessageHandlerException("No encoder defined", message.getClass());
        }
        //TODO implement a pool for packets
        return new Packet(handler.opcode, handler.encoder.encode(message));
    }

}
