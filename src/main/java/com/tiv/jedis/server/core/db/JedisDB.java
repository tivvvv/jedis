package com.tiv.jedis.server.core.db;

import com.tiv.jedis.server.core.data.JedisData;
import com.tiv.jedis.server.core.structure.Dict;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * Jedis数据库.
 */
@Getter
@Setter
public class JedisDB {

    /**
     * 数据库id.
     */
    private final int id;

    private final Dict<byte[], JedisData> dict;

    public JedisDB(int id) {
        this.id = id;
        this.dict = new Dict<>();
    }

    public Set<byte[]> keySet() {
        return dict.keySet();
    }

    public boolean exists(byte[] key) {
        return dict.containsKey(key);
    }

    public void put(byte[] key, JedisData value) {
        dict.put(key, value);
    }

    public JedisData get(byte[] key) {
        return dict.get(key);
    }

    public JedisData remove(byte[] key) {
        return dict.remove(key);
    }

    public int size() {
        return dict.size();
    }

}
