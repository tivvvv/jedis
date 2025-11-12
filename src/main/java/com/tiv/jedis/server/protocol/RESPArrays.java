package com.tiv.jedis.server.protocol;

import io.netty.buffer.ByteBuf;
import lombok.Getter;

/**
 * RESP数组
 */
@Getter
public class RESPArrays extends RESPProtocol {

    private final RESPProtocol[] content;

    public RESPArrays(RESPProtocol[] content) {
        this.content = content;
    }

    @Override
    public void encode(RESPProtocol respProtocol, ByteBuf byteBuf) {
        // "*2\r\n$3\r\nfoo\r\n$3\r\nbar\r\n"
        byteBuf.writeByte('*');
        byteBuf.writeBytes(String.valueOf(content.length).getBytes());
        for (RESPProtocol resp : content) {
            resp.encode(resp, byteBuf);
        }
        byteBuf.writeBytes(CRLF);
    }

}
