package com.fuzzjump.game.net;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.fuzzjump.server.common.FuzzJumpMessageHandlers;
import com.steveadoo.server.common.packets.Packet;
import com.steveadoo.server.common.packets.PacketProcessor;

import java.io.IOException;
import java.util.List;

public class GameSession implements Client.ConnectionListener {

    public static final int EVENT_TIMEOUT = 50;

    private final String ip;
    private final int port;

    private Pool<ReceivedPacketRunnable> runnablePool;
    private Client client;

    private GameSessionWatcher watcher;

    public GameSession(String ip, int port, GameSessionWatcher watcher) {
        PacketProcessor packetProcessor = new PacketProcessor(FuzzJumpMessageHandlers.HANDLERS);
        this.ip = ip;
        this.port = port;
        this.watcher = watcher;
        this.runnablePool = Pools.get(ReceivedPacketRunnable.class);
        this.client = new Client(packetProcessor, this, EVENT_TIMEOUT, 128);
    }

    public void connect() {
        client.connect(ip, port);
    }

    public void disconnect() {
        try {
            client.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Object message) {
        client.send(message);
    }

    public void close() {
        close(false);
    }

    public void close(boolean ignoreDisconnection) {
        try {
            if (ignoreDisconnection) {
                watcher = null;
            }
            client.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connected() {
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
        postOnUIThread(packet);
    }

    @Override
    public void receivedMessages(List<Packet> packets) {
        postOnUIThread(packets);
    }

    public void postOnUIThread(Packet packet) {
        ReceivedPacketRunnable runnable = runnablePool.obtain();
        runnable.init(this, getPacketProcessor(), packet);
        Gdx.app.postRunnable(runnable);
    }

    public void postOnUIThread(List<Packet> packets) {
        ReceivedPacketRunnable runnable = runnablePool.obtain();
        runnable.init(this, getPacketProcessor(), packets);
        Gdx.app.postRunnable(runnable);
    }

    public PacketProcessor getPacketProcessor() {
        return client.getPacketProcessor();
    }

    public void setGameWatcher(GameSessionWatcher watcher) {
        this.watcher = watcher;
    }

}
