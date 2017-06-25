package com.fuzzjump.server.matchmaking;

import com.fuzzjump.server.base.FuzzJumpPlayer;
import com.fuzzjump.server.common.messages.join.Join;
import com.steveadoo.server.base.Player;
import com.steveadoo.server.base.validation.Validator;
import com.steveadoo.server.common.packets.Validation;

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
        FuzzJumpPlayer fjPlayer = (FuzzJumpPlayer) player;
        if (packet.hasServerSessionKey()) {
            boolean sessionKeyValid = server.validateServerSessionKey(player.getChannel(), packet.getUserId(), packet.getServerSessionKey());
            player.getChannel().writeAndFlush(getJoinResponse(fjPlayer, sessionKeyValid));
            return CompletableFuture.completedFuture(sessionKeyValid);
        } else if (!packet.hasSessionKey()) {
            player.getChannel().writeAndFlush(getJoinResponse(fjPlayer, true));
            return CompletableFuture.completedFuture(false);
        }
        String sessionKey = server.generateKey();
        fjPlayer.setServerSessionKey(sessionKey);
        fjPlayer.setUserId(packet.getUserId());

        CompletableFuture<Boolean> future = new CompletableFuture<>();
        server.getApi().getSessionService().verify(packet.getUserId(), "MATCHMAKING", packet.getSessionKey())
                .map(response -> response != null && response.getBody())
                .onErrorReturn(err -> false)
                .subscribe(validated -> {
                    player.getChannel().writeAndFlush(getJoinResponse(fjPlayer, validated));
                    future.complete(validated);
                });

        return future;
    }

    private Join.JoinResponsePacket getJoinResponse(FuzzJumpPlayer fjPlayer, boolean validated) {
        return Join.JoinResponsePacket.newBuilder()
                .setServerSessionKey(fjPlayer.getServerSessionKey())
                .setRedirect(false)
                .setServerIp(server.getServerInfo().ip)
                .setServerPort(server.getServerInfo().port)
                .setStatus(validated ? Validation.AUTHORIZED : Validation.UNAUTHORIZED)
                .build();
    }

}
