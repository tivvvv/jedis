package com.tiv.jedis.server.command;

import com.tiv.jedis.server.command.impl.Ping;
import com.tiv.jedis.server.core.JedisCore;
import lombok.Getter;

import java.util.function.Function;

/**
 * 命令类型枚举
 */
@Getter
public enum CommandType {

    PING(core -> new Ping());

    private final Function<JedisCore, Command> supplier;

    CommandType(Function<JedisCore, Command> supplier) {
        this.supplier = supplier;
    }

}
