package com.fuzzjump.server.base;

import com.fuzzjump.api.Api;
import com.steveadoo.server.base.Player;
import com.steveadoo.server.base.Server;
import com.steveadoo.server.common.packets.PacketProcessor;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import io.reactivex.schedulers.Schedulers;

public abstract class FuzzJumpServer<T extends FuzzJumpPlayer, E extends FuzzJumpServerInfo> extends Server<E> {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final long RECONNECT_TIMEOUT = 5000;
    private static final AttributeKey<ScheduledFuture> RECONNECT_KEY = AttributeKey.newInstance("Player.reconnectFuture");

    private final Api api;

    private ConcurrentHashMap<String, FuzzJumpPlayer> disconnectedPlayerKeys = new ConcurrentHashMap<>();

    public FuzzJumpServer(E serverInfo, PacketProcessor packetProcessor) {
        super(serverInfo, packetProcessor);
        api = initApi();
    }

    private Api initApi() {
        Api api = new Api.Builder()
                .cachePath("./")
                .url(getServerInfo().apiAddress)
                .build();

        //Authorize. the server will take care of setting the token, we want to make sure our credentials are correct though.
        try {
            api.getUserService()
                    .retrieveToken(getServerInfo().apiUsername, getServerInfo().apiPassword)
                    .observeOn(Schedulers.computation())
                    .toFuture()
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot authorize server with api.");
        }

        return api;
    }

    public boolean validateServerSessionKey(Channel newChannel, String userId, String serverSessionKey) {
        if (!disconnectedPlayerKeys.containsKey(userId)) {
            return false;
        }
        FuzzJumpPlayer player = disconnectedPlayerKeys.get(userId);
        player.setChannel(newChannel);
        boolean valid = player.getServerSessionKey().equals(serverSessionKey);
        if (valid) {
            disconnectedPlayerKeys.remove(userId);
        }
        return valid;
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
        FuzzJumpPlayer fjPlayer = (FuzzJumpPlayer) player;
        disconnectedPlayerKeys.put(fjPlayer.getUserId(), fjPlayer);
        getExecutorService().schedule(() -> {
            if (!disconnectedPlayerKeys.containsKey(fjPlayer.getUserId())) {
                return;
            }
            disconnectedPlayerKeys.remove(fjPlayer.getUserId());
            disconnected(fjPlayer);
        }, RECONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    protected abstract void disconnected(T player);


    public String[] generateKeys(int keyCount) {
        String[] keys = new String[keyCount];
        for(int i = 0; i < keyCount; i++) {
            String key = generateKey();
            keys[i] = key;
        }
        return keys;
    }

    public String generateKey() {
        return new BigInteger(130, SECURE_RANDOM).toString(32);
    }

    public Api getApi() {
        return api;
    }

}
