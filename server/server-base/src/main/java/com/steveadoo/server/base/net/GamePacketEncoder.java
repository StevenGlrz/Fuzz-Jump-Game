package com.steveadoo.server.base.net;

import com.google.protobuf.GeneratedMessage;
import com.steveadoo.server.base.Server;
import com.steveadoo.server.common.packets.Packet;
import com.steveadoo.server.common.packets.PacketProcessor;
import com.steveadoo.server.common.packets.exceptions.MessageHandlerException;
import com.steveadoo.server.common.packets.exceptions.MissingHandlerException;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GamePacketEncoder extends MessageToByteEncoder<Object> {

    private static Logger logger = Logger.getLogger("GamePacketEncoder");

    private final PacketProcessor packetProcessor;

    public GamePacketEncoder(Server server) {
        this.packetProcessor = server.getPacketProcessor();
    }

    public GamePacketEncoder(PacketProcessor packetProcessor) {
        this.packetProcessor = packetProcessor;
    }

    @Override
    protected void encode(final ChannelHandlerContext ctx, final Object msg, final ByteBuf out) throws Exception {
        try {
            Packet packet = packetProcessor.encodeMessage(msg);
            out.writeByte(packet.opcode);
            out.writeByte(packet.length);
            if (packet.length > 0)
                out.writeBytes(packet.data);
        } catch (MissingHandlerException | MessageHandlerException e) {
            logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
    }

}
