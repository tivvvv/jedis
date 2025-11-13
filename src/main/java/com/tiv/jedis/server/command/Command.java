package com.tiv.jedis.server.command;

import com.tiv.jedis.server.protocol.RESPProtocol;

/**
 * 命令接口
 */
public interface Command {

    CommandType getType();

    void setContext(RESPProtocol[] array);

    RESPProtocol handle();

}
