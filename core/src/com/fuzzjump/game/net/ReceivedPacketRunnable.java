package com.fuzzjump.game.net;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.steveadoo.server.common.packets.Packet;
import com.steveadoo.server.common.packets.PacketProcessor;
import com.steveadoo.server.common.packets.exceptions.MessageHandlerException;
import com.steveadoo.server.common.packets.exceptions.MissingHandlerException;

import java.util.List;

public class ReceivedPacketRunnable implements Pool.Poolable, Runnable {

    private GameSession session;
    private PacketProcessor processor;
    private Packet packet;
    private List<Packet> packets;

    public void init(GameSession session, PacketProcessor processor, Packet packet) {
        this.session = session;
        this.processor = processor;
        this.packet = packet;
    }

    public void init(GameSession session, PacketProcessor processor, List<Packet> packets) {
        this.session = session;
        this.processor = processor;
        this.packets = packets;
    }

    @Override
    public void run() {
        try {
            if (packet != null) {
                processor.processPacket(session, packet);
            } else if (packets != null) {
                for (int i = 0, n = packets.size(); i < n; i++) {
                    processor.processPacket(session, packets.get(i));
                }
            }
        } catch (MissingHandlerException e) {
            e.printStackTrace();
        } catch (MessageHandlerException e) {
            e.printStackTrace();
        }
        Pools.free(this);
    }

    @Override
    public void reset() {
        session = null;
        processor = null;
        packet = null;
        packets = null;
    }

}
