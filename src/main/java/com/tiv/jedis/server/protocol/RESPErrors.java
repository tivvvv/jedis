package com.tiv.jedis.server.protocol;

import io.netty.buffer.ByteBuf;

public class RESPErrors extends RESPProtocol {

    private final String content;

    public RESPErrors(String content) {
        this.content = content;
    }

    @Override
    public void encode(RESPProtocol respProtocol, ByteBuf byteBuf) {

    }

}
