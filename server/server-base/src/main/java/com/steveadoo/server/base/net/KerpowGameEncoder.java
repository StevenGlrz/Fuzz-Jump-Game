package com.steveadoo.server.base.net;

import com.google.protobuf.GeneratedMessage;
import com.steveadoo.server.common.packets.Packet;
import com.steveadoo.server.common.packets.PacketHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.logging.Logger;

public class KerpowGameEncoder extends MessageToByteEncoder<GeneratedMessage> {

    private static Logger logger = Logger.getAnonymousLogger();

    private final PacketHandler packetHandler;

    public KerpowGameEncoder(PacketHandler packetHandler) {
        this.packetHandler = packetHandler;
    }

    @Override
    protected void encode(final ChannelHandlerContext ctx, final GeneratedMessage msg, final ByteBuf out) throws Exception {
        Packet packet = packetHandler.getPacket(msg);
        out.writeByte(packet.opcode);
        out.writeByte(packet.length);
        if (packet.length > 0)
            out.writeBytes(packet.data);
    }

}
