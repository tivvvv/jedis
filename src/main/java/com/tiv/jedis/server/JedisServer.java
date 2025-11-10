package com.tiv.jedis.server;

/**
 * jedis服务端接口
 */
public interface JedisServer {

    /**
     * 启动服务
     */
    void start();

    /**
     * 停止服务
     */
    void end();

}
