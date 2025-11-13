package com.tiv.jedis.server.handler;

import com.tiv.jedis.server.command.Command;
import com.tiv.jedis.server.command.CommandType;
import com.tiv.jedis.server.core.JedisCore;
import com.tiv.jedis.server.protocol.RESPArrays;
import com.tiv.jedis.server.protocol.RESPBulkStrings;
import com.tiv.jedis.server.protocol.RESPProtocol;
import com.tiv.jedis.server.protocol.RESPSimpleErrors;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

/**
 * RESP命令处理器
 */
@Slf4j
@Getter
public class RESPCommandHandler extends SimpleChannelInboundHandler<RESPProtocol> {

    private final JedisCore jedisCore;

    public RESPCommandHandler(JedisCore jedisCore) {
        this.jedisCore = jedisCore;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RESPProtocol respProtocol) {
        if (respProtocol instanceof RESPArrays) {
            RESPArrays respArrays = (RESPArrays) respProtocol;
            RESPProtocol response = processCommand(respArrays);

            if (response != null) {
                channelHandlerContext.channel().writeAndFlush(response);
            }
        } else {
            channelHandlerContext.channel().writeAndFlush(new RESPSimpleErrors("参数错误"));
        }
    }

    private RESPProtocol processCommand(RESPArrays respArrays) {
        if (respArrays.getContent().length == 0) {
            return new RESPSimpleErrors("命令为空");
        }

        try {
            RESPProtocol[] array = respArrays.getContent();
            String commandStr = (new String(((RESPBulkStrings) array[0]).getContent())).toUpperCase(Locale.ROOT);
            CommandType commandType;

            try {
                commandType = CommandType.valueOf(commandStr);
            } catch (IllegalArgumentException e) {
                return new RESPSimpleErrors("命令不存在");
            }

            Command command = commandType.getSupplier().apply(jedisCore);
            command.setContext(array);
            return command.handle();
        } catch (Exception e) {
            log.error("processCommand--处理命令异常", e);
            return new RESPSimpleErrors("处理命令异常");
        }
    }
}
