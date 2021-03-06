package org.quanye.sobj.tools;

import org.quanye.sobj.datatype.SObjTable;

import java.util.LinkedList;
import java.util.List;

import static org.quanye.sobj.SObjParser.*;

/**
 * Sexp Tools
 * <p>
 * This source code is license on the Apache-License v2.0
 *
 * @author Quanyec
 */
public class S$ {

    public static String removeBoilerplateEmptyCode(String sexp) {
        /* example:
         * ;; define and obj
         * (*obj ;; obj
         *   (name "DavidChen") ;; name
         *   (age 26))  ;; age
         * ---->
         * (*obj
         *   (name "David")
         *   (age 26))
         */
        String regex = COMMENT_C + "(.*)(\\R)";
        sexp = sexp.replaceAll(regex, "");

        // example: (  a  (  b  c  )  ) -> (a(b  c))
        regex = "(\\s*)(\\" + BRACKET_START_C + ")(\\s*)";
        sexp = sexp.replaceAll(regex, BRACKET_START);
        regex = "(\\s*)(\\" + BRACKET_CLOSE_C + ")(\\s*)";
        sexp = sexp.replaceAll(regex, BRACKET_CLOSE);

        // example: (a(b   c)) -> (a(b c))
        sexp = sexp.replaceAll("\\s+", SEPARATOR);

        return sexp;
    }

    public static String minimizeSexp(String sexp) {
        sexp = removeBoilerplateEmptyCode(sexp);
        // example: (name "David \\\"Chen") -> (name"David \\\"Chen")
        String regex = "(\\s)(\")";
        sexp = sexp.replaceAll(regex, "\"");

        // example: (name 'DavidChen) -> (name'DavidChen)
        regex = "(\\s)(')";
        sexp = sexp.replaceAll(regex, "'");

        // example: (*obj (name"DavidChen")) -> (*obj(name"DavidChen"))
        regex = "(\\s)(\\" + BRACKET_START_C + ")";
        sexp = sexp.replaceAll(regex, BRACKET_START);

        return sexp;
    }

    public static boolean validSexp(String sexp) {
        int sb = 0;
        int cb = 0;
        int i = 0;
        int sLen = sexp.length();
        for (; i < sLen; ++i) {
            if (BRACKET_START_C == sexp.charAt(i)) {
                sb++;
                if (cb >= sb) {
                    return false;
                }
            } else if (BRACKET_CLOSE_C == sexp.charAt(i)) {
                cb++;
                if (cb == sb) {
                    ++i;
                    break;
                }
            }
        }
        return (sb == cb) && (i == sLen);
    }


    public static boolean isNull(String sexp) {
        return sexp.equals(NULL);
    }

    /**
     * Similar to Scheme's `isList` procedure
     **/
    public static boolean isList(String sexp) {
        return sexp.startsWith(BRACKET_START);
    }

    public static boolean isPair(String sexp) {
        if (!sexp.startsWith(BRACKET_START)) {
            return false;
        }
        return !isNull(sexp);
    }


    public static String car(String sexp) {
        int i = 1;
        int sLen = sexp.length();
        if (sexp.charAt(1) == BRACKET_START_C) {
            int bStart = 0;
            int bClose = 0;
            for (; i < sLen; ++i) {
                char c = sexp.charAt(i);
                if (c == BRACKET_START_C) {
                    bStart++;
                } else if (c == BRACKET_CLOSE_C) {
                    bClose++;
                    if (bClose >= bStart) {
                        i++;
                        break;
                    }
                }
            }
        } else if (sexp.charAt(1) == '\"') {
            for (i = 2; i < sLen; ++i) {
                char c = sexp.charAt(i);
                if (c == '\\') {
                    if ((i + 1) < sLen && sexp.charAt(i + 1) == '\"') {
                        i++;
                    }
                } else if (c == '\"') {
                    i++;
                    break;
                }
            }
        } else if (sexp.charAt(1) == '\'') {
            return '\'' + car(sexp.substring(1, sLen));
        } else {
            for (; i < sLen; ++i) {
                char c = sexp.charAt(i);
                if (c == BRACKET_START_C || c == SEPARATOR_C || c == BRACKET_CLOSE_C || c == '\"') {
                    break;
                }
            }
        }
        return sexp.substring(1, i);
    }

    public static String cdr(String sexp) {
        int i = car(sexp).length();
        if (sexp.length() > i + 1 && sexp.charAt(i + 1) == SEPARATOR_C) {
            i++;
        }
        return BRACKET_START + sexp.substring(i + 1);
    }

    public static long length(String sObj) {
        if (isNull(sObj)) {
            return 0;
        } else {
            return 1 + length(cdr(sObj));
        }
    }

    public static long deep(String sObj) {
        if (!isList(sObj) || isNull(sObj) || sObj.isEmpty()) {
            return 0;
        } else {
            return Long.max(1 + deep(car(sObj)), deep(cdr(sObj)));
        }
    }

    private static String first(String sexp) {
        return car(sexp);
    }

    private static String second(String sexp) {
        return car(cdr(sexp));
    }

    private static String third(String sexp) {
        return car(cdr(cdr(sexp)));
    }

    public static SObjTable<Object, Object> toArraySObjTable(SObjTable<Object, Object> table, int index, String sObj) {
        String ele = car(sObj);
        if (isPair(ele)) {
            if (!isNull(cdr(sObj))) {
                table.put(index, toSObjTable(new SObjTable<>(), ele));
                toArraySObjTable(table, index + 1, cdr(sObj));
            } else {
                table.put(index, toSObjTable(new SObjTable<>(), ele));
            }
        } else if (isNull(cdr(sObj))) {
            table.put(index, C$.toTypeValue(ele));
        } else {
            table.put(index, C$.toTypeValue(ele));
            toArraySObjTable(table, index + 1, cdr(sObj));
        }
        return table;
    }

    public static SObjTable<Object, Object> toSObjTable(SObjTable<Object, Object> table, String sObj) {
        String ele = car(sObj);
        if (ele.equals(OBJECT_NAME)) {
            toSObjTable(table, cdr(sObj));
        } else if (ele.equals(LIST_NAME)) {
            toArraySObjTable(table, 0, cdr(sObj));
        } else if (isPair(ele)) {
            String firValue = first(ele);
            String secValue = second(ele);
            if (!isPair(secValue)) {
                if (!isNull(cdr(sObj))) {
                    table.put(firValue, C$.toTypeValue(secValue));
                    toSObjTable(table, cdr(sObj));
                } else {
                    table.put(firValue, C$.toTypeValue(secValue));
                }
            } else {
                if (!isNull(cdr(sObj))) {
                    table.put(firValue, toSObjTable(new SObjTable<>(), secValue));
                    toSObjTable(table, cdr(sObj));
                } else {
                    table.put(firValue, toSObjTable(new SObjTable<>(), secValue));
                }
            }
        }
        return table;
    }

    public static String toArrayJSON(String sObj) {
        String ele = car(sObj);
        if (isPair(ele)) {
            if (!isNull(cdr(sObj))) {
                return toJSON(ele) + "," + toArrayJSON(cdr(sObj));
            } else {
                return toJSON(ele);
            }
        } else if (isNull(cdr(sObj))) {
            return C$.trimSObjBoolToNormalBool(ele);
        } else {
            return ele + "," + toArrayJSON(cdr(sObj));
        }
    }

    public static String toJSON(String sObj) {
        String ele = car(sObj);
        if (ele.equals(OBJECT_NAME)) {
            return "{" + toJSON(cdr(sObj)) + "}";
        } else if (ele.equals(LIST_NAME)) {
            return "[" + toArrayJSON(cdr(sObj)) + "]";
        } else if (isPair(ele)) {
            String firValue = first(ele);
            String secValue = second(ele);
            if (!isPair(secValue)) {
                if (!isNull(cdr(sObj))) {
                    return "\"" + firValue + "\":" + secValue + "," + toJSON(cdr(sObj));
                } else {
                    return "\"" + firValue + "\":" + C$.trimSObjBoolToNormalBool(secValue);
                }
            } else {
                if (!isNull(cdr(sObj))) {
                    return "\"" + firValue + "\":" + toJSON(secValue) + "," + toJSON(cdr(sObj));
                } else {
                    return "\"" + firValue + "\":" + toJSON(secValue);
                }
            }
        } else {
            // Is atom
            if (!isNull(cdr(sObj))) {
                return C$.trimSObjBoolToNormalBool(ele);
            } else {
                return "null";
            }
        }
    }

    private static String beautifyList(String sObj, int spaces) {
        StringBuilder rc = new StringBuilder();
        StringBuilder sc = new StringBuilder("\n");
        for (int i = 0; i < spaces; ++i) {
            sc.append("  ");
        }
        if (!isNull(sObj)) {
            rc.append(beautify(car(sObj), spaces + 1));
            if (!isNull(cdr(sObj))) {
                rc.append(sc);
            }
            rc.append(beautifyList(cdr(sObj), spaces));
        }
        return rc.toString();
    }

    private static String beautify(String sObj, int spaces) {
        StringBuilder rc = new StringBuilder();
        StringBuilder sc = new StringBuilder("\n");
        for (int i = 0; i < spaces; ++i) {
            sc.append("  ");
        }
        if (!isList(sObj)) {
            rc.append(sObj);
        } else if (C$.isSObj(sObj)) {
            rc.append(BRACKET_START_C)
                    .append(first(sObj))
                    .append(beautify(cdr(sObj), spaces))
                    .append(BRACKET_CLOSE_C);
        } else if (C$.isList(sObj)) {
            rc.append(BRACKET_START_C)
                    .append(first(sObj))
                    .append(sc)
                    .append(beautifyList(cdr(sObj), spaces))
                    .append(BRACKET_CLOSE_C);
        } else if (!isNull(sObj)) {
            rc.append(sc).append(BRACKET_START_C).append(first(first(sObj))).append(SEPARATOR_C)
                    .append(beautify(second(first(sObj)), spaces + 1))
                    .append(BRACKET_CLOSE_C)
                    .append(beautify(cdr(sObj), spaces));
        }
        return rc.toString();
    }

    public static String beautify(String sObj) {
        return beautify(sObj, 1);
    }
}
