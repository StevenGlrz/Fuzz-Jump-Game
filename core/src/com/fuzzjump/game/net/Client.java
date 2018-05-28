package com.fuzzjump.game.net;

import com.steveadoo.server.common.packets.Packet;
import com.steveadoo.server.common.packets.PacketProcessor;
import com.steveadoo.server.common.packets.exceptions.MessageHandlerException;
import com.steveadoo.server.common.packets.exceptions.MissingHandlerException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class Client {

    private final int selectorTimeout;
    private final ByteBuffer readBuffer;

    private ConnectionListener listener;
    private Selector selector;
    private SocketChannel socket;

    private String ip;
    private int port;
    private InetSocketAddress address;

    private PacketProcessor packetProcessor;
    private final Queue<Object> writeQueue = new LinkedList<>();

    private boolean connected = false;
    private Thread thread;

    public Client(PacketProcessor packetProcessor, ConnectionListener listener, int selectorTimeout, int readBufferSize) {
        this.packetProcessor = packetProcessor;
        this.selectorTimeout = selectorTimeout;
        this.listener = listener;
        this.readBuffer = ByteBuffer.allocate(readBufferSize);
    }

    public void send(Object message) {
        synchronized (writeQueue) {
            writeQueue.offer(message);
        }
    }

    public void connect(String ip, int port) {
        this.ip = ip;
        this.port = port;
        if (thread != null) {
            try {
                disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        thread = new Thread(this::connect);
        thread.start();
    }

    public void disconnect() throws IOException {
        if (thread == null || selector == null || socket == null)
            return;
        thread.interrupt();
        selector.close();
        socket.close();
        thread = null;
        selector = null;
        socket = null;
    }

    private void connect() {
        try {
            address = new InetSocketAddress("127.0.0.1", port);
            initSocket();
            connected = false;
            while (!Thread.interrupted()) {

                //pretty sure this does nothing
                if ((connected && !socket.isConnected()))
                    break;

                //do this here rather than in send. key.interestOps will block if the selector is happening
                //so we can end up blocking the calling thread.
                checkWrite();

                selector.select(selectorTimeout);
                if (!selector.isOpen())
                    continue;

                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                while (keys.hasNext() && selector.isOpen()) {
                    SelectionKey key = keys.next();
                    keys.remove();
                    handleKey(key);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (listener != null) {
            listener.disconnected();
        }
    }

    private void initSocket() throws IOException {
        selector = Selector.open();
        socket = SocketChannel.open();
        socket.socket().setTcpNoDelay(true);
        socket.socket().setSendBufferSize(256);
        socket.socket().setReceiveBufferSize(256);
        socket.socket().setKeepAlive(true);
        socket.configureBlocking(false);
        socket.register(selector, SelectionKey.OP_CONNECT);
        System.out.println("Connecting to " + address.getHostName() + ":" + address.getPort());
        socket.connect(address);
    }

    private void checkWrite() {
        synchronized (writeQueue) {
            if (writeQueue.peek() != null) {
                SelectionKey rwKey = socket.keyFor(selector);
                rwKey.interestOps(rwKey.interestOps() | SelectionKey.OP_WRITE);
            }
        }
    }

    private void handleKey(SelectionKey key) throws IOException {
        if (!key.isValid()) {
            return;
        }
        if (key.isConnectable()) {
            if (connect(key)) {
                connected = true;
            }
        }
        if (key.isReadable()) {
            read(key);
        }
        if (key.isWritable()) {
            try {
                if (write(key)) {
                    SelectionKey rwKey = socket.keyFor(selector);
                    rwKey.interestOps(rwKey.interestOps() & (~SelectionKey.OP_WRITE));
                }
            } catch (MissingHandlerException | MessageHandlerException e) {
                e.printStackTrace();
            }
        }
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        readBuffer.clear();

        int length = 0;
        int totalLength = 0;
        while ((length = channel.read(readBuffer)) > 0) {
            totalLength += length;

            if (!readBuffer.hasRemaining()) {
                break;
            }
        }
        if (length == -1) {
            channel.close();
            key.cancel();
            return;
        }
        if (totalLength > 0) {
            readBuffer.flip();
            parse(key, readBuffer);
        }
    }

    private void parse(SelectionKey key, ByteBuffer buffer) throws IOException {
        while (buffer.hasRemaining()) {
            int opcode = buffer.get() & 0xFF;
            int len = buffer.get() & 0xFF;

            if (buffer.remaining() < len) {
                // TODO Handle this
                return;
            }

            byte[] data = new byte[len];
            buffer.get(data);
            Packet packet = new Packet(opcode, data);

            listener.receivedMessage(packet);
        }
    }

    private boolean write(SelectionKey key) throws IOException, MissingHandlerException, MessageHandlerException {
        SocketChannel channel = (SocketChannel) key.channel();
        Object message = null;
        ByteBuffer buffer = null;
        Packet packet = null;
        synchronized (writeQueue) {
            while ((message = writeQueue.peek()) != null) {
                packet = packetProcessor.encodeMessage(message);
                buffer = ByteBuffer.allocateDirect(2 + packet.length);
                buffer.put((byte) packet.opcode);
                buffer.put((byte) packet.length);
                if (packet.length > 0)
                    buffer.put(packet.data);
                buffer.flip();
                if (channel.write(buffer) == 0) {
                    //return false so it will wait until sendbuffer is cleared
                    return false;
                }
                buffer.clear();
                writeQueue.remove();
            }
        }
        return true;
    }

    private boolean connect(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        if (channel.isConnectionPending()) {
            if (!channel.finishConnect()) {
                return false;
            }
        }
        if (!channel.isConnected())
            return false;
        channel.configureBlocking(false);
        socket.register(selector, SelectionKey.OP_READ);
        if (listener != null) {
            listener.connected();
        }
        return true;
    }

    public PacketProcessor getPacketProcessor() {
        return packetProcessor;
    }

    public void removeListener() {
        listener = null;
    }

    public interface ConnectionListener {

        void connected();

        void disconnected();

        void receivedMessage(Packet packet);

    }
}
