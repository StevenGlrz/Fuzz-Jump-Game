package com.fuzzjump.server.common;

import com.fuzzjump.server.common.messages.lobby.Lobby;
import com.steveadoo.server.common.Join;
import com.steveadoo.server.common.packets.MessageHandler;
import com.steveadoo.server.common.packets.Packets;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FuzzJumpMessageHandlers {

    public static final List<MessageHandler> handlers;

    static {
        List tempHandlerList = new LinkedList<>();
        tempHandlerList.add(MessageHandler.create(Packets.JOIN_PACKET, Join.JoinPacket.getDefaultInstance()));
        tempHandlerList.add(MessageHandler.create(Packets.JOIN_PACKET_RESPONSE, Join.JoinResponsePacket.getDefaultInstance()));
        tempHandlerList.add(MessageHandler.create(FuzzJumpPackets.LOBBY_STATE, Lobby.LobbyState.getDefaultInstance()));
        tempHandlerList.add(MessageHandler.create(FuzzJumpPackets.LOBBY_STATE, Lobby.LobbyState.getDefaultInstance()));
        tempHandlerList.add(MessageHandler.create(FuzzJumpPackets.TIME_STATE_UPDATE, Lobby.TimeState.getDefaultInstance()));
        tempHandlerList.add(MessageHandler.create(FuzzJumpPackets.MAP_SLOT_VOTES_UPDATE, Lobby.MapSlot.getDefaultInstance()));
        tempHandlerList.add(MessageHandler.create(FuzzJumpPackets.MAP_SLOT_UPDATE, Lobby.MapSlotSet.getDefaultInstance()));
        tempHandlerList.add(MessageHandler.create(FuzzJumpPackets.READY_UPDATE, Lobby.ReadySet.getDefaultInstance()));
        tempHandlerList.add(MessageHandler.create(FuzzJumpPackets.LOBBY_LOADED, Lobby.Loaded.getDefaultInstance()));
        tempHandlerList.add(MessageHandler.create(FuzzJumpPackets.GAME_FOUND, Lobby.GameFound.getDefaultInstance()));
        handlers = Collections.unmodifiableList(tempHandlerList);
    }
}
