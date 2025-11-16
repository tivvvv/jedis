package com.tiv.jedis.server.core.structure;

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
        h0 = new DictHashTable<>(INITIAL_SIZE);
        h1 = null;
        rehashIndex = -1;
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
     * 字典哈希表
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
     * 字典节点
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
