package com.steveadoo.server.base;

import com.steveadoo.server.base.validation.Validator;
import com.steveadoo.server.common.packets.Packet;
import com.steveadoo.server.common.packets.PacketProcessor;

import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

public abstract class Server<TInfo extends ServerInfo> implements PacketProcessor.ProcessPipeline {

    public static final AttributeKey<Player> PLAYER_ATTRIBUTE_KEY = AttributeKey.newInstance("Channel.player");
    public static final AttributeKey<Boolean> VALIDATED_ATTR_KEY = AttributeKey.newInstance("Channel.validated");

    protected final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private final LinkedList<Validator> validators;
    private final PacketProcessor packetProcessor;
    private final TInfo serverInfo;

    public Server(TInfo serverInfo, PacketProcessor packetProcessor) {
        this.serverInfo = serverInfo;
        this.packetProcessor = packetProcessor;
        this.packetProcessor.setProcessPipeline(this);
        this.validators = new LinkedList<>();
    }

    public void addValidator(Validator validator) {
        validators.add(validator);
    }

    public Object transformSender(Object sender, Packet packet) {
        return ((Channel) sender).attr(PLAYER_ATTRIBUTE_KEY).get();
    }

    public final boolean checkMessage(Object sender, Packet packet, Object message) {
        Player player = (Player) sender;
        if (serverInfo.validate) {
            Boolean validated = player.getChannel().attr(VALIDATED_ATTR_KEY).get();
            if (validated == null || !validated) {
                System.out.println("Player is not validated. Trying validation.");
                //trigger validators
                tryValidate(player, packet, message);
                return false;
            }
        }
        return true;
    }

    public final void onConnect(Channel channel) {
        Player player = createPlayer(channel);
        channel.attr(PLAYER_ATTRIBUTE_KEY).set(player);
        if (serverInfo.validate) {
            System.out.println("Validations are on. Waiting " + serverInfo.validationTimeout + "ms for validation packet until removing the player");
            executorService.schedule(() -> {
                System.out.println("Checking if player sent validation packet.");
                Boolean validated = player.getChannel().attr(VALIDATED_ATTR_KEY).get();
                if (validated == null || !validated) {
                    System.out.println("Player did not send validation packet. Disconnecting them.");
                    player.getChannel().attr(Server.PLAYER_ATTRIBUTE_KEY).remove();
                    player.getChannel().close();
                } else {
                    System.out.println("Player is validated.");
                }
            }, serverInfo.validationTimeout, TimeUnit.MILLISECONDS);
        } else {
            connected(player);
        }
    }

    public final void onDisconnect(Channel channel) {
        Player player = channel.attr(PLAYER_ATTRIBUTE_KEY).get();
        if (player == null) {
            return;
        }
        disconnected(player);
    }

    private void tryValidate(Player player, Packet packet, Object message) {
        for (Validator validator : validators) {
            if (!validator.matches(message.getClass(), message)) {
                continue;
            }
            System.out.println(validator.getClass().getSimpleName() + " is validating player");
            validator.validate(player, message).thenAccept(validated -> {
                System.out.println(validator.getClass().getSimpleName() + " validator validation complete. Player valid? " + validated);
                if (!validated) {
                    return;
                }
                onValidated(player);
            });
            return;
        }
    }

    private void onValidated(Player player) {
        player.getChannel().attr(VALIDATED_ATTR_KEY).set(true);
        connected(player);
    }

    public final PacketProcessor getPacketProcessor() {
        return packetProcessor;
    }

    public final TInfo getServerInfo() {
        return serverInfo;
    }

    public final ScheduledExecutorService getExecutorService() {
        return executorService;
    }

    /**
     * Called when a player connects
     * TODO move this out into some sort of PlayerHandler?
     * @param player the player that connected
     */
    protected void connected(Player player) {
    }

    /**
     * Called when a player disconnects
     * TODO move this out into some sort of PlayerHandler?
     * @param player the player that disconnected
     */
    protected void disconnected(Player player) {
    }

    /**
     * Creates a Player object to attach to this channel
     * TODO move this out into some sort of PlayerHandler?
     * @param channel the netty channel
     * @return the created player
     */
    protected abstract Player createPlayer(Channel channel);

}
