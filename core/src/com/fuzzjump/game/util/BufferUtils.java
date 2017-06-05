package com.fuzzjump.game.util;

import java.nio.ByteBuffer;

public class BufferUtils {

    public static void putString(ByteBuffer buffer, String string) {
        if (string.length() > Byte.MAX_VALUE)
            string = string.substring(0, Byte.MAX_VALUE);
        buffer.put((byte) string.length());
        buffer.put(string.getBytes());
    }

    public static String readString(ByteBuffer buffer) {
        int len = buffer.get() & 0xFF;
        byte[] data = new byte[len];
        buffer.get(data, 0, len);
        return new String(data);
    }

}
