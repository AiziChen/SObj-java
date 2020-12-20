package org.quanye.sobj;

import org.quanye.sobj.annotation.DateFormat;
import org.quanye.sobj.datatype.SObjTable;
import org.quanye.sobj.exception.InvalidSObjSyntaxException;
import org.quanye.sobj.tools.C$;
import org.quanye.sobj.tools.S$;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * SObj Parser
 * SObj stand for `symbolic object`
 * This source code is license on the Apache-License v2.0
 *
 * @author QuanyeChen
 */
public class SObjParser {
    private static final SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.FORMAT_STYLE);
    public static final String BRACKET_START = "(";
    public static final char BRACKET_START_C = '(';
    public static final String BRACKET_CLOSE = ")";
    public static final char BRACKET_CLOSE_C = ')';
    public static final String BRACKET_OBJECT = "(*obj";
    public static final String OBJECT_NAME = "*obj";
    public static final String BRACKET_LIST = "(*list";
    public static final String LIST_NAME = "*list";
    public static final String SEPARATOR = " ";
    public static final char SEPARATOR_C = ' ';
    public static final String NULL = "()";
    public static final char COMMENT_C = ';';
    public static final String FALSE_VALUE = "#f";
    public static final String TRUE_VALUE = "#t";

    /**
     * Parse the Java Object to the SObj
     *
     * @param obj Object
     * @return SObj
     */
    public static String fromObject(Object obj) {
        StringBuilder result = new StringBuilder();
        Class<?> clazz = obj.getClass();
        if (clazz.isArray()) {
            // When clazz is an array
            Object[] values = (Object[]) obj;
            StringBuilder sb = new StringBuilder();
            sb.append(BRACKET_LIST);
            for (Object v : values) {
                if (v instanceof String) {
                    sb.append('\"').append(v).append('\"');
                } else {
                    sb.append(fromObject(v));
                }
            }
            sb.append(BRACKET_CLOSE);
            result.append(sb);
        } else {
            // Otherwise clazz is an object
            StringBuilder sb = new StringBuilder(BRACKET_OBJECT);// + obj.getClass().getSimpleName());
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object value;
                try {
                    value = field.get(obj);
                } catch (IllegalAccessException e) {
                    continue;
                }
                if (value != null) {
                    if (value instanceof String) {
                        value = "\"" + value + "\"";
                    } else if (value instanceof Date) {
                        Object tmp = value;
                        value = "\"" + sdf.format(value) + "\"";
                        for (Annotation an : field.getAnnotations()) {
                            if (an instanceof DateFormat) {
                                DateFormat df = (DateFormat) an;
                                sdf.applyPattern(df.value());
                                value = "\"" + sdf.format((Date) tmp) + "\"";
                            }
                        }
                    } else if (value instanceof Boolean) {
                        value = ((boolean) value) ? TRUE_VALUE : FALSE_VALUE;
                    } else if (value.getClass().getClassLoader() != null) {
                        // If clazz is a user-defined type(in there must the POJO-type), then extract it
                        value = fromObject(value);
                    } else if (value.getClass().isArray()) {
                        value = fromObject(value);
                    }
                    String name = field.getName();
                    sb.append(BRACKET_START).append(name).append(SEPARATOR_C).append(value).append(BRACKET_CLOSE);
                }
            }
            sb.append(BRACKET_CLOSE);
            result.append(sb);
        }
        return result.toString();
    }

    /**
     * Parse the SObj and override the `instance`
     *
     * @param sObj     SObj
     * @param instance Override instance
     * @param <T>      instance type
     * @return overrode instance
     * @throws InvalidSObjSyntaxException Throws the exception while the SObj syntax is non-valid
     */
    public static <T> T toObject(String sObj, T instance) throws InvalidSObjSyntaxException {
        sObj = S$.removeBoilerplateEmptyCode(sObj);
        if (!S$.validSexp(sObj)) {
            throw new InvalidSObjSyntaxException("Invalid SObj syntax");
        }
        SObjTable<String, Object> lo = new SObjTable<>();
        Class<?> clazz = instance.getClass();
        if (clazz.isArray()) {
            throw new RuntimeException("Array override is not support.");
        } else {
            return setValue(sObj, instance);
        }
    }


    /**
     * Parse the SObj to the Java Object
     *
     * @param sObj  SObj
     * @param clazz Resulting object's type
     * @param <T>   Object's generic type
     * @return Object
     * @throws InvalidSObjSyntaxException Throws the exception while the SObj syntax is non-valid
     */
    public static <T> T toObject(String sObj, Class<T> clazz) throws InvalidSObjSyntaxException {
        sObj = S$.removeBoilerplateEmptyCode(sObj);
        if (!S$.validSexp(sObj)) {
            throw new InvalidSObjSyntaxException("Invalid SObj syntax");
        }
        if (clazz.isArray()) {
            Class<?> compClazz = clazz.getComponentType();
            String pkgName = compClazz.getPackage().getName();
            return setArrayValue(sObj, pkgName, compClazz.getSimpleName());
        } else {
            try {
                return setValue(sObj, clazz.getDeclaredConstructor().newInstance());
            } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                return null;
            }
        }
    }


    private static <T> T setArrayValue(String sObj, String pkgName, String compClazzName) {
        List<Object> list = new LinkedList<>();
        String arrEleNode = S$.cdr(sObj);
        if (C$.isSObj(S$.car(arrEleNode))) {
            try {
                Class<?> compClazz = Class.forName(pkgName + "." + compClazzName);
                while (S$.isPair(arrEleNode) && S$.isPair(S$.car(arrEleNode))) {
                    Object instance = setValue(S$.car(arrEleNode), compClazz.getDeclaredConstructor().newInstance());
                    list.add(instance);
                    arrEleNode = S$.cdr(arrEleNode);
                }
                int lSize = list.size();
                if (lSize > 0) {
                    Object target = Array.newInstance(compClazz, lSize);
                    for (int i = 0; i < lSize; ++i) {
                        Array.set(target, i, list.get(i));
                    }
                    return (T) target;
                }
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            String carV = S$.car(arrEleNode);
            while (S$.isPair(arrEleNode)) {
                String v = S$.car(arrEleNode);
                v = C$.trimStr(v);
                list.add(v);
                arrEleNode = S$.cdr(arrEleNode);
            }
            int lSize = list.size();
            if (!S$.isNull(carV) && lSize > 0) {
                Object target = Array.newInstance(C$.getValueType(carV), lSize);
                for (int i = 0; i < lSize; ++i) {
                    Array.set(target, i, list.get(i));
                }
                return (T) target;
            }
        }
        return null;
    }


    private static <T> T setValue(String sObj, T target) {
        String firstV = S$.car(sObj);
        String leftV = S$.cdr(sObj);
        // Key
        if (!S$.isNull(firstV) && !S$.isNull(leftV)) {
            if (!(firstV.equals(OBJECT_NAME) || firstV.equals(LIST_NAME))) {
                String key = firstV;
                String value = S$.car(leftV);
                if (C$.isSObj(value)) {
                    String pkgName = target.getClass().getPackage().getName();
                    String clazzName = key.substring(0, 1).toUpperCase() + key.substring(1);
                    try {
                        Class<?> clazz = Class.forName(pkgName + "." + clazzName);
                        Object instance = setValue(S$.car(leftV), clazz.getDeclaredConstructor().newInstance());
                        putField(target, key, instance);
                    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } else if (C$.isList(value)) {
                    String pkgName = target.getClass().getPackage().getName();
                    String clazzName = key.substring(0, 1).toUpperCase() + key.substring(1);
                    T arrInstance = setArrayValue(S$.car(leftV), pkgName, clazzName);
                    putField(target, key, arrInstance);
                    // *list process had been done above, don't need to process by `setValue` ever.
                    return arrInstance;
                } else if (!S$.isList(value)) {
                    value = C$.trimStr(value);
                    putField(target, key, value);
                }
            }
        }

        // car is a list
        if (!S$.isNull(firstV) && S$.isList(firstV)) {
            setValue(firstV, target);
        }

        // not end list
        if (!S$.isNull(leftV) && S$.isList(leftV)) {
            setValue(leftV, target);
        }

        return target;
    }

    private static <T> void putField(T target, String key, String value) {
        Field field = C$.getFieldByName(target, key);
        if (field != null) {
            field.setAccessible(true);
            Class<?> typeClazz = field.getType();
            if (C$.isPrimitive(typeClazz)) {
                // Change `TURE_VALUE` and `FALSE_VALUE` to `true` and `false`
                value = C$.trimSObjBoolToNormalBool(value);
                try {
                    Constructor<?> c = typeClazz.getConstructor(String.class);
                    field.set(target, c.newInstance(value));
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else {
                // Other type
                if (typeClazz.getName().equals("java.util.Date")) {
                    for (Annotation an : field.getAnnotations()) {
                        if (an instanceof DateFormat) {
                            DateFormat df = (DateFormat) an;
                            sdf.applyPattern(df.value());
                            break;
                        }
                    }
                    try {
                        field.set(target, sdf.parse(value));
                    } catch (ParseException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    private static void putField(Object target, String key, Object instance) {
        Field field = C$.getFieldByName(target, key);
        if (field != null) {
            field.setAccessible(true);
            try {
                field.set(target, instance);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}
