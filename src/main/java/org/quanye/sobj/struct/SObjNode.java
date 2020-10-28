package org.quanye.sobj.struct;

import org.quanye.sobj.SObjParser;
import org.quanye.sobj.exception.InvalidSObjSyntaxException;
import org.quanye.sobj.tools.C$;
import org.quanye.sobj.tools.S$;

import java.lang.reflect.InvocationTargetException;

/**
 * SObjNode
 * `SObj` itself is a structured data, so it can don't need to parse
 *
 * @author Quanyec
 */
public class SObjNode {
    private final String nodeValue;

    public SObjNode(String sObj) {
        this.nodeValue = sObj;
    }

    public SObjNode getNode(String key) {
        SObjNode node = getCdr();
        while (true) {
            SObjNode pair = node.getCar();
            if (pair.getCar().nodeValue.equals(key)) {
                return pair.getCdr().getCar();
            } else {
                node = node.getCdr();
                if (node == null) {
                    return null;
                }
            }
        }
    }

    public <T> T getValue(Class<T> clazz) {
        String keysValue = nodeValue;
        if (S$.isPair(keysValue)) {
            try {
                return SObjParser.toObject(keysValue, clazz);
            } catch (InvalidSObjSyntaxException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            try {
                return clazz.getConstructor(String.class).newInstance(C$.trimStr(keysValue));
            } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public SObjNode getCar() {
        if (S$.isPair(nodeValue)) {
            return new SObjNode(S$.car(nodeValue));
        } else {
            return null;
        }
    }

    public SObjNode getCdr() {
        if (S$.isPair(nodeValue)) {
            String nv = S$.cdr(nodeValue);
            if (S$.isNull(nv)) {
                return null;
            } else {
                return new SObjNode(nv);
            }
        } else {
            return null;
        }
    }

    public String getNodeValue() {
        return nodeValue;
    }

    /**
     * Count nodeValue's length
     *
     * @return equal to 0 where nodeValue is `Null`, or -1 where nodeValue is not a List, or the List element length
     */
    public long length() {
        if (S$.isList(nodeValue)) {
            return S$.length(nodeValue);
        } else {
            return -1;
        }
    }

    public boolean isList() {
        return S$.isList(nodeValue);
    }

    public SObjNode listIndex(int index) {
        assert index >= 0;
        return listIndex(getCdr(), index);
    }

    private SObjNode listIndex(SObjNode node, int index) {
        if (node == null) {
            return null;
        } else if (index <= 0) {
            return node.getCar();
        } else {
            return listIndex(node.getCdr(), index - 1);
        }
    }
}
