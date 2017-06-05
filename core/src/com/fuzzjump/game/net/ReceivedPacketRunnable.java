package com.fuzzjump.game.net;

import java.util.List;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.kerpowgames.server.common.packets.Packet;
import com.kerpowgames.server.common.packets.PacketHandler;

/**
 * Created by Steveadoo on 12/29/2015.
 */
public class ReceivedPacketRunnable implements Pool.Poolable, Runnable {

    private GameSession session;
    private PacketHandler handler;
    private Packet packet;
    private List<Packet> packets;

    public void init(GameSession session, PacketHandler handler, Packet packet) {
        this.session = session;
        this.handler = handler;
        this.packet = packet;
    }

    public void init(GameSession session, PacketHandler handler, List<Packet> packets) {
        this.session = session;
        this.handler = handler;
        this.packets = packets;
    }

    @Override
    public void run() {
        if (packet != null) {
            handler.packetReceived(session, packet);
        } else if (packets != null) {
            for (int i = 0, n = packets.size(); i < n; i++) {
                handler.packetReceived(session, packets.get(i));
            }
        }
        Pools.free(this);
    }

    @Override
    public void reset() {
        session = null;
        handler = null;
        packet = null;
        packets = null;
    }

}
