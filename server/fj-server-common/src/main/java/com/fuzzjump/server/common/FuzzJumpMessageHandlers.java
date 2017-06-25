package com.fuzzjump.server.common;

import com.fuzzjump.server.common.messages.join.Join;
import com.fuzzjump.server.common.messages.lobby.Lobby;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.steveadoo.server.common.packets.MessageHandler;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FuzzJumpMessageHandlers {

    public static final List<MessageHandler> HANDLERS;

    private static final MessageHandler.Encoder<? extends GeneratedMessage> ENCODER = new MessageHandler.Encoder<GeneratedMessage>() {
        @Override
        public byte[] encode(GeneratedMessage message) {
            return message.toByteArray();
        }
    };

    static {
        List<MessageHandler> tempHandlerList = new LinkedList<>();
        tempHandlerList.add(createProtoMessageHandler(FuzzJumpPackets.JOIN_PACKET, Join.JoinPacket.getDefaultInstance()));
        tempHandlerList.add(createProtoMessageHandler(FuzzJumpPackets.JOIN_PACKET_RESPONSE, Join.JoinResponsePacket.getDefaultInstance()));
        tempHandlerList.add(createProtoMessageHandler(FuzzJumpPackets.LOBBY_STATE, Lobby.LobbyState.getDefaultInstance()));
        tempHandlerList.add(createProtoMessageHandler(FuzzJumpPackets.LOBBY_STATE, Lobby.LobbyState.getDefaultInstance()));
        tempHandlerList.add(createProtoMessageHandler(FuzzJumpPackets.TIME_STATE_UPDATE, Lobby.TimeState.getDefaultInstance()));
        tempHandlerList.add(createProtoMessageHandler(FuzzJumpPackets.MAP_SLOT_VOTES_UPDATE, Lobby.MapSlot.getDefaultInstance()));
        tempHandlerList.add(createProtoMessageHandler(FuzzJumpPackets.MAP_SLOT_UPDATE, Lobby.MapSlotSet.getDefaultInstance()));
        tempHandlerList.add(createProtoMessageHandler(FuzzJumpPackets.READY_UPDATE, Lobby.ReadySet.getDefaultInstance()));
        tempHandlerList.add(createProtoMessageHandler(FuzzJumpPackets.LOBBY_LOADED, Lobby.Loaded.getDefaultInstance()));
        tempHandlerList.add(createProtoMessageHandler(FuzzJumpPackets.GAME_FOUND, Lobby.GameFound.getDefaultInstance()));
        tempHandlerList.add(createProtoMessageHandler(FuzzJumpPackets.JOIN_SERVER_PACKET, Join.JoinServerPacket.getDefaultInstance()));
        tempHandlerList.add(createProtoMessageHandler(FuzzJumpPackets.GAME_SERVER_FOUND, Lobby.GameServerFound.getDefaultInstance()));
        tempHandlerList.add(createProtoMessageHandler(FuzzJumpPackets.GAME_SERVER_SETUP, Lobby.GameServerSetup.getDefaultInstance()));
        tempHandlerList.add(createProtoMessageHandler(FuzzJumpPackets.GAME_SERVER_SETUP_RESPONSE, Lobby.GameServerSetupResponse.getDefaultInstance()));
        tempHandlerList.add(createProtoMessageHandler(FuzzJumpPackets.GAME_SERVER_SETUP_DATA, Lobby.GameServerSetupData.getDefaultInstance()));
        HANDLERS = Collections.unmodifiableList(tempHandlerList);
    }

    public static MessageHandler createProtoMessageHandler(int opcode, final GeneratedMessage message) {
        MessageHandler.Decoder decoder = new MessageHandler.Decoder() {
            @Override
            public GeneratedMessage decode(byte[] data) {
                try {
                    return message.getParserForType().parseFrom(data);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
        return new MessageHandler(message.getClass(), opcode, decoder, ENCODER);
    }
}
