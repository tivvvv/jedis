package com.tiv.jedis.server.core.impl;

import com.tiv.jedis.server.core.JedisCore;
import com.tiv.jedis.server.core.data.JedisData;
import com.tiv.jedis.server.core.db.JedisDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class JedisCoreImpl implements JedisCore {

    private final List<JedisDB> databases;

    private final int dbNum;

    private int currentDBIndex = 0;

    public JedisCoreImpl(int dbNum) {
        this.dbNum = dbNum;
        this.databases = new ArrayList<>();
        for (int i = 0; i < dbNum; i++) {
            databases.add(new JedisDB(i));
        }
    }

    @Override
    public Set<byte[]> keys() {
        JedisDB jedisDB = databases.get(getCurrentDBIndex());
        return jedisDB.keySet();
    }

    @Override
    public void put(byte[] key, JedisData value) {
        JedisDB jedisDB = databases.get(getCurrentDBIndex());
        jedisDB.put(key, value);
    }

    @Override
    public JedisData get(byte[] key) {
        JedisDB jedisDB = databases.get(getCurrentDBIndex());
        if (jedisDB.exists(key)) {
            return jedisDB.get(key);
        }
        return null;
    }

    @Override
    public JedisData remove(byte[] key) {
        JedisDB jedisDB = databases.get(getCurrentDBIndex());

        return jedisDB.remove(key);
    }

    @Override
    public int getDBNum() {
        return dbNum;
    }

    @Override
    public int getCurrentDBIndex() {
        return currentDBIndex;
    }

    @Override
    public void selectDB(int dbIndex) {
        if (dbIndex < 0 || dbIndex >= dbNum) {
            throw new RuntimeException("dbIndex out of range");
        }
        currentDBIndex = dbIndex;
    }

}
