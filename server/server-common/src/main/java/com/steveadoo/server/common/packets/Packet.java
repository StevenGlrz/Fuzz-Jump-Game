package com.steveadoo.server.common.packets;

public class Packet {

    public final int opcode;
    public final int length;
    public final byte[] data;

    public Packet(int opcode, byte[] data) {
        this.opcode = opcode;
        this.data = data;
        this.length = data == null ? 0 : data.length;
    }

}
