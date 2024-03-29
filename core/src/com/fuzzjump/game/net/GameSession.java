package com.fuzzjump.game.net;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.fuzzjump.server.common.FuzzJumpMessageHandlers;
import com.steveadoo.server.common.packets.Packet;
import com.steveadoo.server.common.packets.PacketProcessor;

import java.io.IOException;

public class GameSession implements Client.ConnectionListener {

    public static final int EVENT_TIMEOUT = 50;

    private final String ip;
    private final int port;

    private Pool<ReceivedPacketRunnable> runnablePool;
    private Client client;

    private GameSessionWatcher watcher;
    private boolean connected;

    public GameSession(String ip, int port, GameSessionWatcher watcher) {
        this.ip = ip;
        this.port = port;
        this.watcher = watcher;
        this.runnablePool = Pools.get(ReceivedPacketRunnable.class);
        this.client = new Client(new PacketProcessor(FuzzJumpMessageHandlers.HANDLERS), this, EVENT_TIMEOUT, 256);
    }

    public void connect() {
        connected = false;
        client.connect(ip, port);
    }

    public void send(Object message) {
        client.send(message);
    }

    public void close() {
        close(false);
    }

    public void close(boolean ignoreDisconnection) {
        try {
            connected = false;
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
        connected = true;
        if (watcher != null) {
            Gdx.app.postRunnable(() -> {
                if (watcher != null) {
                    watcher.onConnect();
                }
            });
        }
    }

    @Override
    public void disconnected() {
        connected = false;
        if (watcher != null) {
            Gdx.app.postRunnable(() -> {
                if (watcher != null) {
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

    public void postOnUIThread(Packet packet) {
        ReceivedPacketRunnable runnable = runnablePool.obtain();
        runnable.init(this, getPacketProcessor(), packet);
        Gdx.app.postRunnable(runnable);
    }

    public PacketProcessor getPacketProcessor() {
        return client.getPacketProcessor();
    }

    public void setGameWatcher(GameSessionWatcher watcher) {
        this.watcher = watcher;
    }

    public boolean isConnected() {
        return connected;
    }

}
