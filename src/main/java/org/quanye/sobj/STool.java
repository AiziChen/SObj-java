package org.quanye.sobj;

import org.quanye.sobj.exception.InvalidSObjSyntaxException;
import org.quanye.sobj.tools.C$;
import org.quanye.sobj.tools.S$;

/**
 * SObj Tools
 *
 * @author Quanyec
 */
public class STool {

    public static String toJSON(String sObj) throws InvalidSObjSyntaxException {
        sObj = S$.removeBoilerplateEmptyCode(sObj);
        if (!S$.isValidSexp(sObj)) {
            throw new InvalidSObjSyntaxException("Invalid SObj syntax");
        }
        if (C$.isSObj(sObj)) {
            return S$.toJSON(sObj);
        } else {
            return S$.toArrayJSON(sObj);
        }
    }

    public static String minimize(String sObj) {
        return S$.minimizeSexp(sObj);
    }

    public static String beautify(String sObj) {
        return S$.beautify(sObj);
    }
}
