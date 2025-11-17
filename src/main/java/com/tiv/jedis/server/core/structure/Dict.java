package com.tiv.jedis.server.core.structure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 底层结构-dict
 *
 * @param <K>
 * @param <V>
 */
public class Dict<K, V> {

    private DictHashTable<K, V> h0;

    private DictHashTable<K, V> h1;

    private int rehashIndex;

    /**
     * 初始容量
     */
    private static final int INITIAL_SIZE = 16;

    /**
     * 负载因子
     */
    private static final double LOAD_FACTOR = 0.75;

    /**
     * 单步rehash最大处理桶数
     */
    private static final int MAX_REHASH_PROCESSED_NUM = 5;

    public Dict() {
        clear();
    }

    /**
     * 获取key对应的value
     *
     * @param key
     * @return
     */
    public V get(K key) {
        DictEntry<K, V> entry = find(key);
        return entry == null ? null : entry.value;
    }

    /**
     * 获取key对应的节点
     *
     * @param key
     * @return
     */
    private DictEntry<K, V> find(K key) {
        if (key == null) {
            return null;
        }

        if (rehashIndex != -1) {
            rehashStep();
        }

        int index = calIndex(key, h0.size);
        DictEntry<K, V> entry = h0.table[index];
        while (entry != null) {
            if (entry.key.equals(key)) {
                return entry;
            }
            entry = entry.next;
        }

        if (rehashIndex != -1 && h1 != null) {
            index = calIndex(key, h1.size);
            entry = h1.table[index];
            while (entry != null) {
                if (entry.key.equals(key)) {
                    return entry;
                }
                entry = entry.next;
            }
        }
        return null;
    }

    /**
     * 添加key-value
     *
     * @param key
     * @param value
     * @return
     */
    public V put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }

        if (rehashIndex == -1 && (double) h0.used / h0.size >= LOAD_FACTOR) {
            // 达到负载因子,开始rehash
            startRehash();
        }

        DictEntry<K, V> entry = find(key);
        if (entry != null) {
            V oldValue = entry.value;
            entry.value = value;
            return oldValue;
        }

        if (rehashIndex != -1) {
            int index = calIndex(key, h1.size);
            DictEntry<K, V> newEntry = new DictEntry<>(key, value, h1.table[index]);
            h1.table[index] = newEntry;
            h1.used++;
        } else {
            int index = calIndex(key, h0.size);
            DictEntry<K, V> newEntry = new DictEntry<>(key, value, h0.table[index]);
            h0.table[index] = newEntry;
            h0.used++;
        }
        return null;
    }

    /**
     * 删除key
     *
     * @param key
     * @return
     */
    public V remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        if (rehashIndex != -1) {
            rehashStep();
        }
        int index = calIndex(key, h0.size);
        DictEntry<K, V> entry = h0.table[index];
        DictEntry<K, V> prev = null;

        while (entry != null) {
            if (key.equals(entry.key)) {
                if (prev == null) {
                    h0.table[index] = entry.next;
                } else {
                    prev.next = entry.next;
                }
                h0.used--;
                return entry.value;
            }
            prev = entry;
            entry = entry.next;
        }

        if (rehashIndex != -1 && h1 != null) {
            index = calIndex(key, h1.size);
            entry = h1.table[index];
            prev = null;
            while (entry != null) {
                if (key.equals(entry.key)) {
                    if (prev == null) {
                        h1.table[index] = entry.next;
                    } else {
                        prev.next = entry.next;
                    }
                    h1.used--;
                    return entry.value;
                }
                prev = entry;
                entry = entry.next;
            }
        }
        return null;
    }

    /**
     * 获取所有key
     *
     * @return
     */
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        if (rehashIndex != -1) {
            rehashStep();
        }
        for (int i = 0; i < h0.size; i++) {
            DictEntry<K, V> entry = h0.table[i];
            while (entry != null) {
                keys.add(entry.key);
                entry = entry.next;
            }
        }
        if (rehashIndex != -1 && h1 != null) {
            for (int i = 0; i < h1.size; i++) {
                DictEntry<K, V> entry = h1.table[i];
                while (entry != null) {
                    keys.add(entry.key);
                    entry = entry.next;
                }
            }
        }
        return keys;
    }

    /**
     * 获取所有key-value
     *
     * @return
     */
    public Map<K, V> keyValueMap() {
        Map<K, V> map = new HashMap<>();
        if (rehashIndex != -1) {
            rehashStep();
        }
        for (int i = 0; i < h0.size; i++) {
            DictEntry<K, V> entry = h0.table[i];
            while (entry != null) {
                map.put(entry.key, entry.value);
                entry = entry.next;
            }
        }
        if (rehashIndex != -1 && h1 != null) {
            for (int i = 0; i < h1.size; i++) {
                DictEntry<K, V> entry = h1.table[i];
                while (entry != null) {
                    map.put(entry.key, entry.value);
                    entry = entry.next;
                }
            }
        }
        return map;
    }

    /**
     * 清空dict
     */
    public void clear() {
        h0 = new DictHashTable<>(INITIAL_SIZE);
        h1 = null;
        rehashIndex = -1;
    }

    /**
     * 获取dict大小
     *
     * @return
     */
    public int size() {
        return h0.used + (h1 == null ? 0 : h1.used);
    }

    /**
     * 计算索引
     *
     * @param key
     * @param size
     * @return
     */
    private int calIndex(K key, int size) {
        return hash(key) & (size - 1);
    }

    /**
     * 计算hash值
     *
     * @param key
     * @return
     */
    private int hash(K key) {
        if (key == null) {
            return 0;
        }

        int hc = key.hashCode();
        // 确保hash值是正数
        return (hc ^ (hc >>> 16)) & 0x7fffffff;
    }

    /**
     * 开始rehash
     */
    private void startRehash() {
        h1 = new DictHashTable<>(h0.size * 2);
        rehashIndex = 0;
    }

    /**
     * 单步rehash
     */
    private void rehashStep() {
        if (h1 == null || rehashIndex == -1) {
            return;
        }

        // 已处理桶数
        int processed = 0;

        while (rehashIndex < h0.size && processed < MAX_REHASH_PROCESSED_NUM) {
            DictEntry<K, V> entry = h0.table[rehashIndex];
            if (entry == null) {
                rehashIndex++;
                continue;
            }

            DictEntry<K, V> next;
            while (entry != null) {
                next = entry.next;
                int index = calIndex(entry.key, h1.size);
                entry.next = h1.table[index];
                h1.table[index] = entry;
                h0.used--;
                h1.used++;
                entry = next;
            }

            h0.table[rehashIndex++] = null;
            processed++;
        }
        if (rehashIndex >= h0.size && h0.used == 0) {
            h0 = h1;
            h1 = null;
            rehashIndex = -1;
        }
    }

    /**
     * dict哈希表
     *
     * @param <K>
     * @param <V>
     */
    private static class DictHashTable<K, V> {

        private DictEntry<K, V>[] table;

        private int size;

        private int used;

        @SuppressWarnings("unchecked")
        public DictHashTable(int size) {
            this.table = (DictEntry<K, V>[]) new DictEntry[size];
            this.size = size;
            this.used = 0;
        }

    }

    /**
     * dict节点
     *
     * @param <K>
     * @param <V>
     */
    private static class DictEntry<K, V> {

        private K key;

        private V value;

        private DictEntry<K, V> next;

        public DictEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public DictEntry(K key, V value, DictEntry<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

    }

}
