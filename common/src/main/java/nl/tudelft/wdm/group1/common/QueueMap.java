package nl.tudelft.wdm.group1.common;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class QueueMap<K, V> {
    private Map<K, Queue<V>> internal;

    public QueueMap() {
        internal = new HashMap<>();
    }

    public V poll(K key) {
        return internal.get(key).poll();
    }

    public V put(K key, V value) {
        if (internal.containsKey(key)) {
            internal.get(key).add(value);
        } else {
            Queue<V> queue = new LinkedList<>();
            queue.add(value);
            internal.put(key, queue);
        }
        return value;
    }
}
