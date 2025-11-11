package com.tiv.jedis.server.protocol;

import io.netty.buffer.ByteBuf;

public class RESPIntegers extends RESPProtocol {

    private final int content;

    public RESPIntegers(int content) {
        this.content = content;
    }

    @Override
    public void encode(ByteBuf byteBuf) {

    }

}
