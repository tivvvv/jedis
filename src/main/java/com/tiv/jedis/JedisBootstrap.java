package com.tiv.jedis;

import com.tiv.jedis.server.JedisServer;
import com.tiv.jedis.server.impl.JedisServerImpl;

/**
 * Jedis启动类
 */
public class JedisBootstrap {

    public static void main(String[] args) {
        JedisServer jedisServer = new JedisServerImpl("localhost", 16378);
        jedisServer.start();
    }

}
