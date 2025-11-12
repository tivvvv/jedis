package com.tiv.jedis.server.protocol;

import io.netty.buffer.ByteBuf;
import lombok.Data;

/**
 * RESP批量字符串
 */
@Data
public class RESPBulkStrings extends RESPProtocol {

    private static final byte[] NULL_BYTES = "-1\r\n".getBytes();

    private static final byte[] EMPTY_BYTES = "0\r\n\r\n".getBytes();

    private final byte[] content;

    public RESPBulkStrings(byte[] content) {
        this.content = content;
    }

    @Override
    public void encode(RESPProtocol respProtocol, ByteBuf byteBuf) {
        // "$6\r\nfoobar\r\n"
        byteBuf.writeByte('$');
        if (content == null) {
            byteBuf.writeBytes(NULL_BYTES);
        } else {
            if (content.length == 0) {
                byteBuf.writeBytes(EMPTY_BYTES);
            } else {
                byteBuf.writeBytes(String.valueOf(content.length).getBytes());
                byteBuf.writeBytes(CRLF);
                byteBuf.writeBytes(content);
                byteBuf.writeBytes(CRLF);
            }
        }
    }

}
