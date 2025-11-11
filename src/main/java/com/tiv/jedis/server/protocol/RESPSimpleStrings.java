package com.tiv.jedis.server.protocol;

import io.netty.buffer.ByteBuf;

public class RESPSimpleStrings extends RESPProtocol {

    private final String content;

    public RESPSimpleStrings(String content) {
        this.content = content;
    }

    @Override
    public void encode(ByteBuf byteBuf) {

    }

}
