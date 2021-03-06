package test;

import org.junit.Test;
import org.quanye.sobj.tools.S$;

import static org.quanye.sobj.tools.S$.validSexp;
import static org.quanye.sobj.tools.S$.removeBoilerplateEmptyCode;

public class SExpTest {

    @Test
    public void removeBoilerplateEmptyCodeTest() {
        // example 1: simple
        String s = "(  a  (  b    c  )  )";
        s = removeBoilerplateEmptyCode(s);
        assert s.equals("(a(b c))");

        s = "(  a  (  b    \"abc\"  )  )";
        s = removeBoilerplateEmptyCode(s);
        assert s.equals("(a(b \"abc\"))");

        s = "(  a  (  b    \"1 \"abc\" 1\"  )  )";
        s = removeBoilerplateEmptyCode(s);
        assert s.equals("(a(b \"1 \"abc\" 1\"))");

        // example 2: complex and contains comments
        String s2 = "(sobj  ;; listen port\n" +
                "    (listenPort 7878)\r\n" +
                "    ;; destinations\n" +
                "    (destinations\r\n" +
                "     (list\n" +
                "        (sobj (port 8081) (paths (list \"/\")))\n" +
                "        (sobj (port 8080) (paths (list \"/service/\" \"/serve/\")))))\n" +
                "    ;; others\n" +
                ")\n";
        s2 = removeBoilerplateEmptyCode(s2);
        assert s2.equals("(sobj(listenPort 7878)(destinations(list(sobj(port 8081)(paths(list \"/\")))(sobj(port 8080)(paths(list \"/service/\" \"/serve/\"))))))");
    }

    @Test
    public void isValidSexpTest() {
        String s = "(  a  (  b    c  'd   \" E F \" 'G  )  )";
        assert validSexp(s);

        s = "(obj (a b )";
        assert !validSexp(s);

        s = "(obj (a b )))";
        assert !validSexp(s);

        s = "(()) ()";
        assert !validSexp(s);

        s = ")))(((";
        assert !validSexp(s);

        s = "\"hello\"";
        assert validSexp(s);

        s = "112";
        assert validSexp(s);

        s = "2.23";
        assert validSexp(s);
    }

    @Test
    public void lengthTest() {
        String sexp = "(a b c)";
        assert S$.length(sexp) == 3;

        sexp = "((a b) c d e)";
        assert S$.length(sexp) == 4;

        sexp = "((((a b)) c) d e)";
        assert S$.length(sexp) == 3;

        sexp = "((((a b)) c) \"d\" 'e)";
        assert S$.length(sexp) == 3;

        sexp = "((((a b)) c) 'e \"d\")";
        assert S$.length(sexp) == 3;

        sexp = "()";
        assert S$.length(sexp) == 0;

        sexp = "((1))";
        assert S$.length(sexp) == 1;

        sexp = "((()()()))";
        assert S$.length(sexp) == 1;
    }

    @Test
    public void deepTest() {
        String sexp = "(a b c)";
        assert S$.deep(sexp) == 1;

        sexp = "((a b) c d e)";
        assert S$.deep(sexp) == 2;

        sexp = "((((a b)) c) d e)";
        assert S$.deep(sexp) == 4;

        sexp = "(((q y) (g j) ((a b)) c) d e)";
        assert S$.deep(sexp) == 4;

        sexp = "(((q y) (g (q (+ 1 2)) j) ((a b)) c) d e)";
        assert S$.deep(sexp) == 5;
    }
}
