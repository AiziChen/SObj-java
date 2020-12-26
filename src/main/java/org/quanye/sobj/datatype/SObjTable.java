package org.quanye.sobj.datatype;

import java.lang.reflect.Field;
import java.util.Hashtable;

public class SObjTable<K, V> extends Hashtable<K, V> {
    public SObjTable<K, V> getNode(K key) {
        return (SObjTable<K, V>) get(key);
    }

    public <T> T getValue(K key, Class<T> clazz) throws Exception {
        V value = get(key);
        if (value instanceof SObjTable) {
            return stToObject(clazz, (SObjTable<K, V>) value);
        } else {
            return (T) get(key);
        }
    }

    public V listIndex(K index) {
        return get(index);
    }

    public int listLength() {
        return size();
    }

    public <T> T toObject(Class<T> clazz, SObjTable<K, V> st) throws Exception {
        return stToObject(clazz, st);
    }

    private <T> T stToObject(Class<T> clazz, SObjTable<K, V> st) throws Exception {
        T tObj = clazz.getDeclaredConstructor().newInstance();
        String pkgName = clazz.getPackage().getName();
        for (K key : st.keySet()) {
            V value = st.get(key);
            for (Field f : clazz.getDeclaredFields()) {
                if (f.getName().equals(key)) {
                    f.setAccessible(true);
                    if (value instanceof SObjTable) {
                        SObjTable<K, V> nst = (SObjTable<K, V>) value;
                        String skey = (String) key;
                        String clazzName = skey.substring(0, 1).toUpperCase() + skey.substring(1);
                        Class<?> sclazz = Class.forName(pkgName + "." + clazzName);
                        T nt = (T) stToObject(sclazz, nst);
                        f.set(tObj, nt);
                    } else {
                        if (value instanceof Double) {
                            Float fv = Float.parseFloat(value + "");
                            try {
                                f.set(tObj, fv);
                            } catch (Exception e) {
                                f.set(tObj, value);
                            }
                        } else {
                            f.set(tObj, value);
                        }
                    }
                }
            }
        }
        return tObj;
    }
}
