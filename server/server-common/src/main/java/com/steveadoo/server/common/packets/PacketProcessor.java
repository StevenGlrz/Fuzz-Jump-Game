package com.steveadoo.server.common.packets;

import com.steveadoo.server.common.packets.coder.decoder.PacketDecoder;
import com.steveadoo.server.common.packets.coder.encoder.PacketEncoder;
import com.steveadoo.server.common.packets.exceptions.MessageHandlerException;
import com.steveadoo.server.common.packets.exceptions.MissingHandlerException;

import java.util.LinkedList;
import java.util.List;

public class PacketProcessor {

    private final PacketDecoder decoder;
    private final PacketEncoder encoder;

    private final List<ListenerWrapper> listeners;

    private ProcessPipeline pipeline;

    public PacketProcessor(PacketDecoder decoder, PacketEncoder encoder) {
        this.listeners = new LinkedList<>();
        this.decoder = decoder;
        this.encoder = encoder;
    }

    public PacketProcessor(List<MessageHandler> handlers) {
        this(new PacketDecoder(handlers), new PacketEncoder(handlers));
    }

    public <T> T decodePacket(Packet packet) throws MissingHandlerException, MessageHandlerException {
        return decoder.decodePacket(packet);
    }

    public Packet encodeMessage(Object message) throws MissingHandlerException, MessageHandlerException {
        return encoder.encodePacket(message);
    }

    /**
     * Processes a received packet
     * @param sender The sender(in Netty, this is the Channel)
     * @param packet The packet received
     * @throws MissingHandlerException If there is no message handler for this packet id
     * @throws MessageHandlerException If there is an error with the message handler for this packet id
     */
    @SuppressWarnings("unchecked")
    public void processPacket(Object sender, Packet packet) throws MissingHandlerException, MessageHandlerException {
        Object message = decodePacket(packet);
        if (pipeline != null) {
            //if the sender transformer returns null then its an invalid session
            sender = pipeline.transformSender(sender, packet);
            if (!pipeline.checkMessage(sender, packet, message)) {
                return;
            }
        }
        for (ListenerWrapper wrapper : listeners) {
            if (message.getClass().equals(wrapper.clazz)) {
                wrapper.listener.received(sender, message);
            }
        }
    }

    public <TSender, TMessage> PacketProcessor addListener(Class<TMessage> mClass, PacketListener<TSender, TMessage> listener) {
        listeners.add(new ListenerWrapper(mClass, listener));
        return this;
    }

    /**
     * Sets the pipeline for transforming or cancelling received packets
     * @param pipeline the pipeline
     */
    public void setProcessPipeline(ProcessPipeline pipeline) {
        this.pipeline = pipeline;
    }

    private class ListenerWrapper {

        public final Class<?> clazz;
        public final PacketListener listener;

        public ListenerWrapper(Class<?> clazz, PacketListener listener) {
            this.clazz = clazz;
            this.listener = listener;
        }

    }

    public interface PacketListener<TSender, TMessage> {

        void received(TSender sender, TMessage message);

    }

    /**
     * WHAT SHOULD I NAME THIS :(
     */
    public interface ProcessPipeline {

        /**
         * Changes the sender to another type.
         * @param sender The sender
         * @param packet The packet, we check the packet id rather than go through and decode the packet
         * @return the transformed sender
         */
        Object transformSender(Object sender, Packet packet);

        /**
         * Checks the packet. This is called after transformSender
         * @param sender The sender
         * @param packet The packet
         * @param message The decoded message
         * @return false if you want to cancel raising the event
         */
        boolean checkMessage(Object sender, Packet packet, Object message);

    }

}
