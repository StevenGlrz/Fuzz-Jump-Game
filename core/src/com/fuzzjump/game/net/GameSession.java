package com.fuzzjump.game.net;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.google.protobuf.GeneratedMessage;
import com.fuzzjump.game.FuzzJump;
import com.kerpowgames.fuzzjump.common.FuzzJumpMessageHandlers;
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressing;

import java.io.IOException;
import java.util.List;

public class GameSession implements Connection.ConnectionListener {

    public static String MATCHMAKING_IP = "";//System.getProperty("matchmaking_ip");//"fj-matchmakingserver-462470304.us-west-2.elb.amazonaws.com";
    public static int MATCHMAKING_PORT = -1;

    private static final int LB_PORT = 6894;

    public static final int EVENT_TIMEOUT = 50;
    private final String ip;
    private final int port;

    private Pool<ReceivedPacketRunnable> runnablePool;
    private Connection connection;

    private boolean throughLoadBalancer;
    private String directServerKey;
    private String directServerIp;
    private int directServerPort;

    private GameSessionWatcher watcher;

    public GameSession(String ip, int port, GameSessionWatcher watcher) {
        PacketHandler packetHandler = new PacketHandler(FuzzJumpMessageHandlers.handlers);
        packetHandler.addListener(Join.JoinResponsePacket.class, new PacketHandler.PacketListener<GameSession, Join.JoinResponsePacket>() {
            @Override
            public void received(GameSession gameSession, Join.JoinResponsePacket joinResponsePacket) {
                joinResponse(gameSession, joinResponsePacket);
            }
        });
        this.watcher = watcher;
        this.runnablePool = Pools.get(ReceivedPacketRunnable.class);
        this.connection = new Connection(packetHandler, this, EVENT_TIMEOUT, 128);
        this.throughLoadBalancer = port == LB_PORT;
        this.ip = ip;
        this.port = port;
    }

    public GameSession(String ip, int port, String key, GameSessionWatcher watcher) {
        this(ip, port, watcher);
        directServerKey = key;
    }

    private void joinResponse(GameSession session, Join.JoinResponsePacket message) {
        if (!message.hasStatus()) {
            close();
        } else if (message.getStatus() == MemberSubmissionAddressing.Validation.UNAUTHORIZED) {
            //raise message?
            close();
        } else if (message.getStatus() == Validation.AUTHORIZED) {
            handleAuthorizedResponse(message);
        } else {
            close();
        }
    }

    private void handleAuthorizedResponse(Join.JoinResponsePacket responsePacket) {
        this.directServerPort = responsePacket.getServerPort();
        this.directServerIp = responsePacket.getServerIp();
        if (responsePacket.getRedirect()) {
            //remove the listener from the connection so we dont get the disconnect event
            connection.removeListener();
            this.directServerKey = responsePacket.getServerSessionKey();
            if (watcher != null) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        watcher.onTransferred();
                    }
                });
            }
            throughLoadBalancer = false;
            connection.connect(directServerIp, directServerPort);
        } else {
            if (watcher != null) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        watcher.authenticated();
                    }
                });
            }
        }
    }

    public void connect() {
        this.connection.connect(ip, port);
    }

    public void send(GeneratedMessage message) {
        connection.send(message);
    }

    public void close() {
        close(false);
    }

    public void close(boolean ignoreDisconnection) {
        try {
            if (ignoreDisconnection) {
                watcher = null;
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connected() {
        Join.JoinPacket.Builder joinBuilder = Join.JoinPacket.newBuilder();
        joinBuilder.setProfileId(FuzzJump.Game.getProfile().getProfileId());
        joinBuilder.setVersion(1);
        if (directServerKey == null) {
            joinBuilder.setSessionKey(FuzzJump.Game.getProfile().getSessionKey());
        } else {
            joinBuilder.setServerSessionKey(directServerKey);
            directServerKey = null;
        }
        connection.send(joinBuilder.build());
        if (watcher != null) {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    watcher.onConnect();
                }
            });
        }
    }

    @Override
    public void disconnected() {
        if (watcher != null) {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    watcher.onDisconnect();
                    watcher = null;
                }
            });
        }
    }

    @Override
    public void receivedMessage(Packet packet) {
        //if were going through the load balancer, we can do everything on the same thread
        //because the UI isnt interested in any packets from it
        if (throughLoadBalancer) {
            getPacketHandler().packetReceived(this, packet);
        } else{
            postOnUIThread(packet);
        }
    }

    @Override
    public void receivedMessages(List<Packet> packets) {
        //if were going through the load balancer, we can do everything on the same thread
        //because the UI isnt interested in any packets from it
        if (throughLoadBalancer) {
            for (Packet packet : packets)
                getPacketHandler().packetReceived(this, packet);
        } else{
            postOnUIThread(packets);
        }
    }

    public void postOnUIThread(Packet packet) {
        ReceivedPacketRunnable runnable = runnablePool.obtain();
        runnable.init(this, getPacketHandler(), packet);
        Gdx.app.postRunnable(runnable);
    }

    public void postOnUIThread(List<Packet> packets) {
        ReceivedPacketRunnable runnable = runnablePool.obtain();
        runnable.init(this, getPacketHandler(), packets);
        Gdx.app.postRunnable(runnable);
    }

    public PacketHandler getPacketHandler() {
        return connection.getPacketHandler();
    }

    public void setGameWatcher(GameSessionWatcher watcher) {
        this.watcher = watcher;
    }

}
