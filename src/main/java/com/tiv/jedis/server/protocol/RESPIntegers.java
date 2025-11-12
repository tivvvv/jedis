package com.tiv.jedis.server.protocol;

import io.netty.buffer.ByteBuf;
import lombok.Getter;

import java.nio.charset.StandardCharsets;

/**
 * RESP整数
 */
@Getter
public class RESPIntegers extends RESPProtocol {

    private final int content;

    public RESPIntegers(int content) {
        this.content = content;
    }

    @Override
    public void encode(RESPProtocol respProtocol, ByteBuf byteBuf) {
        // ":0\r\n"
        byteBuf.writeByte(':');
        byteBuf.writeBytes(String.valueOf(((RESPIntegers) respProtocol).getContent()).getBytes(StandardCharsets.UTF_8));
        byteBuf.writeBytes(CRLF);
    }

}
