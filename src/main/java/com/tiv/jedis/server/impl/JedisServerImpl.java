package com.tiv.jedis.server.impl;

import com.tiv.jedis.server.JedisServer;
import com.tiv.jedis.server.handler.StringHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Jedis服务端实现类
 */
public class JedisServerImpl implements JedisServer {

    private String host;

    private int port;

    private EventLoopGroup leaderGroup;

    private EventLoopGroup workerGroup;

    private Channel serverChannel;

    public JedisServerImpl(String host, int port) {
        this.host = host;
        this.port = port;
        this.leaderGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup(4);
    }

    @Override
    public void start() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(leaderGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline channelPipeline = socketChannel.pipeline();
                        channelPipeline.addLast(new StringDecoder());
                        channelPipeline.addLast(new StringHandler());
                        channelPipeline.addLast(new StringEncoder());
                    }
                });
        try {
            this.serverChannel = serverBootstrap.bind(host, port).sync().channel();
            System.out.printf("Jedis 启动 %s:%s%n", host, port);
        } catch (InterruptedException e) {
            e.printStackTrace();
            end();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void end() {
        if (this.leaderGroup != null) {
            this.leaderGroup.shutdownGracefully();
        }
        if (this.workerGroup != null) {
            this.workerGroup.shutdownGracefully();
        }
        if (this.serverChannel != null) {
            this.serverChannel.close();
        }
    }

}
