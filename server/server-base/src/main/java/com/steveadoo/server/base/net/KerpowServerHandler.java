package com.steveadoo.server.base.net;

import com.steveadoo.server.base.Server;
import com.steveadoo.server.common.packets.Packet;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class KerpowServerHandler extends SimpleChannelInboundHandler<Packet> {

	private Server server;

	private Channel lbChannel;

	public KerpowServerHandler(Server server) {
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
		server.getPacketHandler().packetReceived(channelHandlerContext.channel(), packet);
	}
}
