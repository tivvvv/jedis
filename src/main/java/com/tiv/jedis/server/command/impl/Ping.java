package com.tiv.jedis.server.command.impl;

import com.tiv.jedis.server.command.Command;
import com.tiv.jedis.server.command.CommandType;
import com.tiv.jedis.server.protocol.RESPProtocol;
import com.tiv.jedis.server.protocol.RESPSimpleStrings;

/**
 * PING命令
 * printf '*1\r\n$4\r\nPING\r\n' | nc localhost 6379
 */
public class Ping implements Command {

    @Override
    public CommandType getType() {
        return CommandType.PING;
    }

    @Override
    public void setContext(RESPProtocol[] array) {

    }

    @Override
    public RESPProtocol handle() {
        return new RESPSimpleStrings("PONG");
    }

}
