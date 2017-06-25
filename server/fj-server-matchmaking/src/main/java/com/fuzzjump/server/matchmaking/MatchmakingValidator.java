package com.fuzzjump.server.matchmaking;

import com.fuzzjump.server.base.FuzzJumpPlayer;
import com.fuzzjump.server.common.messages.join.Join;
import com.steveadoo.server.base.Player;
import com.steveadoo.server.base.validation.Validator;

import java.util.concurrent.CompletableFuture;

/**
 * Created by Steveadoo on 6/24/2017.
 */

class MatchmakingValidator implements Validator {

    private final MatchmakingServer server;

    public MatchmakingValidator(MatchmakingServer server) {
        this.server = server;
    }

    @Override
    public void init() {

    }

    @Override
    public boolean matches(Class<?> clazz, Object message) {
        return clazz == Join.JoinPacket.class;
    }

    @Override
    public CompletableFuture<Boolean> validate(Player player, Object message) {
        Join.JoinPacket packet = (Join.JoinPacket) message;
        if (packet.hasServerSessionKey()) {
            return CompletableFuture.completedFuture(server.validateServerSessionKey(player.getChannel(), packet.getUserId(), packet.getServerSessionKey()));
        } else if (!packet.hasSessionKey()) {
            return CompletableFuture.completedFuture(false);
        }
        FuzzJumpPlayer fjPlayer = (FuzzJumpPlayer) player;
        String sessionKey = server.generateKey();
        fjPlayer.setServerSessionKey(sessionKey);
        fjPlayer.setUserId(packet.getUserId());

        CompletableFuture<Boolean> future = new CompletableFuture<>();
        server.getApi().getSessionService().verify(packet.getUserId(), "MATCHMAKING", packet.getSessionKey())
                .map(response -> response != null && response.getBody())
                .onErrorReturn(err -> false)
                .subscribe(validated -> {
                    future.complete(validated);
                    player.getChannel().writeAndFlush(getJoinResponse(fjPlayer));
                });

        return future;
    }

    private Join.JoinResponsePacket getJoinResponse(FuzzJumpPlayer fjPlayer) {
        return Join.JoinResponsePacket.newBuilder()
                .setServerSessionKey(fjPlayer.getServerSessionKey())
                .setRedirect(false)
                .setServerIp(server.getServerInfo().ip)
                .setServerPort(server.getServerInfo().port)
                .build();
    }
}
