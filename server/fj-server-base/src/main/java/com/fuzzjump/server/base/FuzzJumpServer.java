package com.fuzzjump.server.base;

import com.steveadoo.server.base.Player;
import com.steveadoo.server.base.Server;
import com.steveadoo.server.common.packets.PacketProcessor;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

public abstract class FuzzJumpServer<T extends FuzzJumpPlayer, E extends FuzzJumpServerInfo> extends Server<E> {

    public static final String MASTER_KEY = "SDRFG$%^7ftgh%^&56udfghT^&*Iftyuj";
    public static final AttributeKey<Boolean> MASTER_KEY_ATTRIB = AttributeKey.newInstance("ConnectionValidator.masterKey");

    public FuzzJumpServer(E serverInfo, PacketProcessor packetProcessor) {
        super(serverInfo, packetProcessor);
    }

    @Override
    protected abstract T createPlayer(Channel channel);

    @Override
    protected void connected(Player player) {
        connected((T) player);
    }

    protected abstract void connected(T player);

    @Override
    protected void disconnected(Player player) {
        disconnected((T) player);
    }

    protected abstract void disconnected(T player);

}
