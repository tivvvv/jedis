package com.tiv.jedis.server.protocol;

import io.netty.buffer.ByteBuf;
import lombok.Getter;

import java.nio.charset.StandardCharsets;

/**
 * RESP简单字符串
 */
@Getter
public class RESPSimpleStrings extends RESPProtocol {

    private final String content;

    public RESPSimpleStrings(String content) {
        this.content = content;
    }

    @Override
    public void encode(RESPProtocol respProtocol, ByteBuf byteBuf) {
        // "+OK\r\n"
        byteBuf.writeByte('+');
        byteBuf.writeBytes(((RESPSimpleStrings) respProtocol).getContent().getBytes(StandardCharsets.UTF_8));
        byteBuf.writeBytes(CRLF);
    }

}
