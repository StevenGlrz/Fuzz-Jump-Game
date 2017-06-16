package com.steveadoo.server.base;

import com.steveadoo.server.common.Join;
import com.steveadoo.server.common.packets.MessageHandler;
import com.steveadoo.server.common.packets.Packets;
import com.steveadoo.server.common.packets.Validation;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.AttributeKey;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ConnectionValidator {

    private static final long KEY_DURATION = 30000;
    public static final String MASTER_KEY = "SDRFG$%^7ftgh%^&56udfghT^&*Iftyuj";
    public static final AttributeKey<Boolean> MASTER_KEY_ATTRIB = AttributeKey.newInstance("ConnectionValidator.masterKey");

    private static Logger logger = Logger.getAnonymousLogger();

    private static final String KEYMETHOD = "AES";
    private static final String CIPER_METHOD = "AES/CBC/PKCS5Padding";

    private static final int VALIDATION_THREADS = Runtime.getRuntime().availableProcessors();
    private final ExecutorService validationExecutor;

    private final Server server;
    private final ConnectionValidated callback;

    private Key key;
    private Cipher encCipher;
    private Cipher decCipher;

    private HashMap<String, Long> keys = new HashMap<>();

    public ConnectionValidator(Server server, ConnectionValidated callback) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        this.server = server;
        this.callback = callback;
        server.getPacketHandler().addHandler(MessageHandler.create(Packets.JOIN_PACKET, Join.JoinPacket.getDefaultInstance()));
        server.getPacketHandler().addHandler(MessageHandler.create(Packets.JOIN_PACKET_RESPONSE, Join.JoinResponsePacket.getDefaultInstance()));
        server.getPacketHandler().addListener(Join.JoinPacket.class, this::onJoinPacketReceived);
        validationExecutor = Executors.newFixedThreadPool(VALIDATION_THREADS);
        server.getScheduledExecutor().scheduleAtFixedRate(this::removeOldKeys, 0, 5000, TimeUnit.MILLISECONDS);
        //initEncryption();
    }

    private void removeOldKeys() {
        synchronized (keys) {
            Iterator<Map.Entry<String, Long>> entries = keys.entrySet().iterator();
            while(entries.hasNext()) {
                Map.Entry<String, Long> entry = entries.next();
                long time = entry.getValue();
                if (System.currentTimeMillis() - time > KEY_DURATION) {
                    entries.remove();
                }
            }
        }
    }

    private void initEncryption() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        KeyGenerator generator = KeyGenerator.getInstance(KEYMETHOD);
        generator.init(128);
        this.key = generator.generateKey();
        this.encCipher = Cipher.getInstance(CIPER_METHOD);
        byte[] ivByte = new byte[encCipher.getBlockSize()];
        IvParameterSpec ivParamsSpec = new IvParameterSpec(ivByte);
        encCipher.init(Cipher.ENCRYPT_MODE, key, ivParamsSpec);
        this.decCipher = Cipher.getInstance(CIPER_METHOD);
        ivByte = new byte[decCipher.getBlockSize()];
        ivParamsSpec = new IvParameterSpec(ivByte);
        decCipher.init(Cipher.DECRYPT_MODE, key, ivParamsSpec);
    }

    private void onJoinPacketReceived(Channel channel, Join.JoinPacket packet) {
        logger.info("Validating");
        validationExecutor.submit(() -> {
            try {
                if (packet.hasServerSessionKey())
                    handleServerSessionValidation(channel, packet);
                else
                    handleProfileSessionValidation(channel, packet);
            } catch (Exception e) {
                e.printStackTrace();
                channel.disconnect();
            }
        });
    }

    private void handleProfileSessionValidation(Channel channel, Join.JoinPacket packet) throws IOException, BadPaddingException, IllegalBlockSizeException {
        logger.info("Validating profile session");
        boolean fromLb = ((InetSocketAddress) channel.localAddress()).getPort() == server.getServerInfo().getLbPort();
        URLConnection connection = new URL(server.getServerInfo().getAuthUri() + "?profileid=" + packet.getProfileId() + "&sessionkey=" + packet.getSessionKey()).openConnection();
        connection.connect();
        HttpURLConnection httpConnection = (HttpURLConnection) connection;
        int code = httpConnection.getResponseCode();
        if (code == HttpResponseStatus.OK.code()) {
            logger.info("checkAndGet profile session");
            String serverSessionKey = createKey();
            channel.writeAndFlush(Join.JoinResponsePacket.newBuilder().setStatus(Validation.AUTHORIZED)
                    .setServerPort(server.getServerInfo().getPrivatePort())
                    .setServerIp(server.getServerInfo().getIp())
                    .setServerSessionKey(serverSessionKey)
                    .setRedirect(fromLb)
                    .buildPartial());
            //if the connection was from the lb, disconnect because they'll be making a connection on the real server soon.
            if (fromLb)
                channel.disconnect();
            else {
                Player player = server.createPlayer(channel, packet.getProfileId());
                server.addPlayer(player);
                callback.validated(player);
            }
        } else {
            logger.info("invalid profile session");
            channel.writeAndFlush(Join.JoinResponsePacket.newBuilder().setStatus(Validation.UNAUTHORIZED).buildPartial());
            channel.disconnect();
        }
    }

    private void handleServerSessionValidation(Channel channel, Join.JoinPacket packet) throws UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        logger.info("Validating server session");
        boolean fromLb = ((InetSocketAddress) channel.localAddress()).getPort() == server.getServerInfo().getLbPort();
        if (!packet.hasServerSessionKey()) {
            channel.writeAndFlush(Join.JoinResponsePacket.newBuilder().setStatus(Validation.UNAUTHORIZED).buildPartial());
            channel.disconnect();
            return;
        }
        System.out.println(packet.getProfileId() + ", " + packet.getServerSessionKey());
        if (validKey(packet.getProfileId(), packet.getServerSessionKey())) {
            boolean addPlayer = true;
            if (packet.getServerSessionKey() == MASTER_KEY) {
                fromLb = false;
                addPlayer = false;
            }
            channel.writeAndFlush(Join.JoinResponsePacket.newBuilder().setStatus(Validation.AUTHORIZED)
                    .setServerPort(server.getServerInfo().getPrivatePort())
                    .setServerIp(server.getServerInfo().getIp())
                    .setServerSessionKey(packet.getServerSessionKey())
                    .setRedirect(fromLb)
                    .buildPartial());
            //if the connection was from the lb, disconnect because they'll be making a connection on the real server soon.
            if (fromLb)
                channel.disconnect();
            else if (addPlayer) {
                Player player = server.createPlayer(channel, packet.getProfileId());
                server.addPlayer(player);
                callback.validated(player);
            } else {
                //master key
                channel.attr(MASTER_KEY_ATTRIB).set(true);
            }
        } else {
            channel.writeAndFlush(Join.JoinResponsePacket.newBuilder().setStatus(Validation.UNAUTHORIZED).buildPartial());
            channel.disconnect();
        }
    }

    private String createKey() throws UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException {
        /*synchronized (encCipher) {
            String key = profileId + "-" + Server.MACHINE_NAME;
            System.out.println("Enckey " + key);
            return new String(encCipher.doFinal(key.getBytes("UTF-8")), "UTF-8");
        }*/
        synchronized (keys) {
            String key = UUID.randomUUID().toString();
            keys.put(key, System.currentTimeMillis());
            return key;
        }
    }

    private boolean validKey(long profileId, String key) throws UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException {
        /*synchronized (decCipher) {
            String targetKey = profileId + "-" + Server.MACHINE_NAME;
            String decKey = new String(decCipher.doFinal(key.getBytes("UTF-8")), "UTF-8");
            System.out.println("decKey " + key);
            return targetKey.equals(decKey);
        }*/
        if (key.equals(MASTER_KEY)) {
            return true;
        }
        synchronized (keys) {
            if (keys.containsKey(key)) {
                keys.remove(key);
                return true;
            }
            return false;
        }
    }

    public String[] generateKeys(int keyCount) throws UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        String[] keys = new String[keyCount];
        for(int i = 0; i < keys.length; i++) {
            keys[i] = createKey();
        }
        return keys;
    }

    public interface ConnectionValidated {

        void validated(Player player);

    }
}
