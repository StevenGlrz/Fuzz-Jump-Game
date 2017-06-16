package com.steveadoo.server.base;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;

/**
 * Created by Steveadoo on 12/28/2015.
 */
public class LbServerStateListener implements Server.ServerStateListener {

    private final ServerBootstrap bootstrap;
    private final InetSocketAddress address;

    private ChannelFuture future;

    public LbServerStateListener(ServerBootstrap bootstrap, int privatePort) throws InterruptedException {
        this.bootstrap = bootstrap;
        this.address = new InetSocketAddress(privatePort);
        future = bootstrap.bind(address).await();
        System.out.println("Listening on " + privatePort + " for loadbalancer");
    }

    @Override
    public void stateChanged(Server.ServerState state) {
        switch(state) {
            case REFUSE_CONNECTIONS:
                future.channel().close();
                System.out.println("Closing load balancer port");
                break;
            case ACCEPT_CONNECTIONS:
                future = bootstrap.bind(address);
                System.out.println("Opening load balancer port");
                break;
        }
    }
}
