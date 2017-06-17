package com.steveadoo.server.base.net;

import com.steveadoo.server.base.Server;
import com.steveadoo.server.common.packets.Packet;
import com.steveadoo.server.common.packets.exceptions.MessageHandlerException;
import com.steveadoo.server.common.packets.exceptions.MissingHandlerException;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GameServerHandler extends SimpleChannelInboundHandler<Packet> {

	private static Logger logger = Logger.getLogger("GameServerHandler");

	private final Server server;

	public GameServerHandler(Server server) {
		this.server = server;
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) {
		server.onConnect(ctx.channel());
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) {
		server.onDisconnect(ctx.channel());
	}

	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
		try {
			server.getPacketProcessor().processPacket(channelHandlerContext.channel(), packet);
		} catch (MissingHandlerException | MessageHandlerException e) {
			logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		}
	}
}
