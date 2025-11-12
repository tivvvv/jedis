package com.tiv.jedis.server.protocol;

import io.netty.buffer.ByteBuf;
import lombok.Getter;

import java.nio.charset.StandardCharsets;

/**
 * RESP简单错误
 */
@Getter
public class RESPSimpleErrors extends RESPProtocol {

    private final String content;

    public RESPSimpleErrors(String content) {
        this.content = content;
    }

    @Override
    public void encode(RESPProtocol respProtocol, ByteBuf byteBuf) {
        // "-Error message\r\n"
        byteBuf.writeByte('-');
        byteBuf.writeBytes(((RESPSimpleErrors) respProtocol).getContent().getBytes(StandardCharsets.UTF_8));
        byteBuf.writeBytes(CRLF);
    }

}
