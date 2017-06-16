package com.steveadoo.server.common.packets;

import com.google.protobuf.GeneratedMessage;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Steveadoo on 12/12/2015.
 */
public class PacketHandler {

    private final HashMap<Class<? extends GeneratedMessage>, MessageHandler> classHandlerMap;
    private final HashMap<Integer, MessageHandler> opcodeHandlerMap;

    private final List<ListenerWrapper> listeners;

    private PacketSenderTransformer senderTransformer;

    public PacketHandler(List<MessageHandler> handlers) {
        this.classHandlerMap = new HashMap<>();
        this.opcodeHandlerMap = new HashMap<>();
        this.listeners = new LinkedList<>();
        init(handlers);
    }

    private void init(List<MessageHandler> handlers) {
        for(MessageHandler handler : handlers) {
            classHandlerMap.put(handler.messageType, handler);
            opcodeHandlerMap.put(handler.opcode, handler);
        }
    }

    public <T extends GeneratedMessage> T getMessage(Packet packet) {
        if (!opcodeHandlerMap.containsKey(packet.opcode))
            return null;
        return (T)opcodeHandlerMap.get(packet.opcode).decoder.decode(packet.data);
    }

    public Packet getPacket(GeneratedMessage message) {
        if (!classHandlerMap.containsKey(message.getClass()))
            return null;
        MessageHandler handler = classHandlerMap.get(message.getClass());
        return new Packet(handler.opcode, message.toByteArray());
    }

    public <TSender, TMessage extends GeneratedMessage> void addListener(Class<TMessage> mClass, PacketListener<TSender, TMessage> listener) {
        listeners.add(new ListenerWrapper(mClass, listener));
    }

    public void packetReceived(Object sender, Packet packet) {
        GeneratedMessage message = getMessage(packet);
        if (senderTransformer != null) {
            //if the sender transformer returns null then its an invalid session
            if ((sender = senderTransformer.checkAndGet(sender, packet)) == null)
                return;
        }
        for (ListenerWrapper wrapper : listeners) {
            if (message.getClass().equals(wrapper.clazz))
                wrapper.listener.received(sender, message);
        }
    }

    public void addHandler(MessageHandler handler) {
        classHandlerMap.put(handler.messageType, handler);
        opcodeHandlerMap.put(handler.opcode, handler);
    }

    public void setPacketTransformer(PacketSenderTransformer validator) {
        this.senderTransformer = validator;
    }

    private class ListenerWrapper {

        public final Class<? extends GeneratedMessage> clazz;
        public final PacketListener listener;

        public ListenerWrapper(Class<? extends GeneratedMessage> clazz, PacketListener listener) {
            this.clazz = clazz;
            this.listener = listener;
        }

    }

    public interface PacketListener<TSender, TMessage extends GeneratedMessage> {

        void received(TSender sender, TMessage message);

    }

    /**
     * WHAT SHOULD I NAME THIS :(
     */
    public interface PacketSenderTransformer<TSender, TPacket extends Packet> {

        Object checkAndGet(TSender sender, TPacket packet);

    }

}
