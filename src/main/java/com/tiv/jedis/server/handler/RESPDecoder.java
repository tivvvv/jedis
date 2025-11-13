package com.tiv.jedis.server.handler;

import com.tiv.jedis.server.protocol.RESPProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * RESP解码器
 */
@Slf4j
public class RESPDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {

        if (byteBuf.readableBytes() > 0) {
            byteBuf.markReaderIndex();
        }

        if (byteBuf.readableBytes() < 4) {
            return;
        }

        try {
            RESPProtocol respProtocol = RESPProtocol.decode(byteBuf);
            if (respProtocol != null) {
                log.debug("解码成功 {}", respProtocol.getClass().getName());
                list.add(respProtocol);
            }
        } catch (Exception e) {
            log.error("decode--解码异常", e);
            byteBuf.resetReaderIndex();
        }
    }

}
