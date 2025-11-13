package com.tiv.jedis.server.handler;

import com.tiv.jedis.server.protocol.RESPProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * RESP编码器
 */
@Slf4j
public class RESPEncoder extends MessageToByteEncoder<RESPProtocol> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RESPProtocol respProtocol, ByteBuf byteBuf) {
        try {
            respProtocol.encode(respProtocol, byteBuf);
        } catch (Exception e) {
            log.error("encode--编码异常", e);
            channelHandlerContext.channel().close();
        }
    }

}
