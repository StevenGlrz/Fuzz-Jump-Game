package com.steveadoo.server.base;

import com.steveadoo.server.common.packets.Packet;
import com.steveadoo.server.common.packets.PacketHandler;
import com.steveadoo.server.common.packets.Packets;
import io.netty.channel.Channel;
import io.netty.util.Attribute;

import javax.crypto.NoSuchPaddingException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public abstract class Server implements PacketHandler.PacketSenderTransformer<Channel, Packet> {

    public static String MACHINE_NAME = findMachineName();

    //tells the server to automatically send and receive the join packet for validation etc,
    //after the packet has been received, validated, and sent back, we tell the implementing server that the channel joined
    protected final ConnectionValidator validator;
    private final ScheduledExecutorService scheduledExecutor;

    private ServerStateListener stateListener;

    private final PacketHandler packetHandler;
    private final ServerInfo serverInfo;

    private ServerState serverState = ServerState.ACCEPT_CONNECTIONS;

    private final HashMap<Channel, Player> players;

    public Server(ServerInfo serverInfo, PacketHandler packetHandler, boolean validate) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        this.players = new HashMap<>();
        this.serverInfo = serverInfo;
        this.packetHandler = packetHandler;
        if (serverInfo.getAuthUri() != null && validate) {
            validator = new ConnectionValidator(this, this::connected);
            packetHandler.setPacketTransformer(this);
        } else {
            validator = null;
        }
    }

    protected final void raiseServerStateChanged(ServerState state) {
        if (stateListener == null)
            return;
        stateListener.stateChanged(serverState = state);
    }

    public final void onConnect(Channel channel) {
        System.out.println("Channel connected");
        if (validator != null)
            return;
        Player player = createPlayer(channel, -1);
        players.put(channel, player);
        connected(player);
    }

    public final void onDisconnect(Channel channel) {
        //servers will need to check if the channel is actually active
        System.out.println("Channel disconnected");
        if (!players.containsKey(channel))
            return;
        disconnected(players.get(channel));
        players.remove(channel);
    }

    @Override
    public Object checkAndGet(Channel channel, Packet packet) {
        //the join and response packets need the channel
        if (packet.opcode == Packets.JOIN_PACKET_RESPONSE || packet.opcode == Packets.JOIN_PACKET)
            return channel;
        //master key, so a server is connecting to a server
        Attribute<Boolean> attribute = channel.attr(ConnectionValidator.MASTER_KEY_ATTRIB);
        if (attribute != null
                && attribute.get() != null
                && attribute.get() == true) {
            return channel;
        }
        //everything else gets a player
        if (!players.containsKey(channel))
            return null;
        return players.get(channel);
    }

    public final void addPlayer(Player player) {
        players.put(player.channel, player);
    }

    public final void setServerStateListener(ServerStateListener stateListener) {
        this.stateListener = stateListener;
    }

    protected final ServerState getState() {
        return serverState;
    }

    public PacketHandler getPacketHandler() {
        return packetHandler;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public ScheduledExecutorService getScheduledExecutor() {
        return scheduledExecutor;
    }

    public abstract Player createPlayer(Channel channel, long profileId);
    public abstract void connected(Player player);
    public abstract void disconnected(Player player);

    public static String findMachineName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            SecureRandom random = new SecureRandom();
            return new BigInteger(130, random).toString(16);
        }
    }

    public interface ServerStateListener {

        void stateChanged(ServerState state);

    }

    public enum ServerState {

        ACCEPT_CONNECTIONS,
        REFUSE_CONNECTIONS

    }

}
