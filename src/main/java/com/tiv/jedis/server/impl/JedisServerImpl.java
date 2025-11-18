package com.tiv.jedis.server.impl;

import com.tiv.jedis.server.JedisServer;
import com.tiv.jedis.server.core.JedisCore;
import com.tiv.jedis.server.core.impl.JedisCoreImpl;
import com.tiv.jedis.server.handler.RESPCommandHandler;
import com.tiv.jedis.server.handler.RESPDecoder;
import com.tiv.jedis.server.handler.RESPEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * Jedis服务端实现类
 */
@Slf4j
public class JedisServerImpl implements JedisServer {

    private static final int DEFAULT_DB_NUM = 16;

    private String host;

    private int port;

    private EventLoopGroup leaderGroup;

    private EventLoopGroup workerGroup;

    private Channel serverChannel;

    private JedisCore jedisCore;

    public JedisServerImpl(String host, int port) {
        this.host = host;
        this.port = port;
        this.leaderGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup(4);
        this.jedisCore = new JedisCoreImpl(DEFAULT_DB_NUM);
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
                        channelPipeline.addLast(new RESPDecoder());
                        channelPipeline.addLast(new RESPCommandHandler(jedisCore));
                        channelPipeline.addLast(new RESPEncoder());
                    }
                });
        try {
            this.serverChannel = serverBootstrap.bind(host, port).sync().channel();
            log.info(String.format("Jedis服务器启动成功 %s:%s", host, port));
        } catch (InterruptedException e) {
            log.error("Jedis服务器启动异常", e);
            end();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void end() {
        try {
            if (this.serverChannel != null) {
                this.serverChannel.close().sync();
            }
            if (this.workerGroup != null) {
                this.workerGroup.shutdownGracefully().sync();
            }
            if (this.leaderGroup != null) {
                this.leaderGroup.shutdownGracefully().sync();
            }
        } catch (InterruptedException e) {
            log.error("Jedis服务器关闭异常", e);
            Thread.currentThread().interrupt();
        }
    }

}
