package com.tiv.jedis.server.core;

import com.tiv.jedis.server.core.data.JedisData;

import java.util.Set;

public interface JedisCore {

    Set<byte[]> keys();

    void put(byte[] key, JedisData value);

    JedisData get(byte[] key);

    JedisData remove(byte[] key);

    int getDBNum();

    int getCurrentDBIndex();

    void selectDB(int dbIndex);

}
