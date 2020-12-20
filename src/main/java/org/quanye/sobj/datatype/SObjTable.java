package org.quanye.sobj.datatype;

import org.quanye.sobj.SObjParser;
import org.quanye.sobj.exception.InvalidSObjSyntaxException;
import org.quanye.sobj.tools.C$;

import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;

public class SObjTable<K, V> extends Hashtable<K, V> {
    public SObjTable<K, V> getNode(K key) {
        return (SObjTable<K, V>) get(key);
    }

    public <T> T getValue(K key, Class<T> clazz) {
        return (T)get(key);
    }

    public V listIndex(K index) {
        return get(index);
    }
}
