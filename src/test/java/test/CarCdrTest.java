package test;

import org.junit.Test;
import org.quanye.sobj.SObjParser;
import org.quanye.sobj.tools.S$;

public class CarCdrTest {
    @Test
    public void carTest() {
        String sexp = "(sobj(listenPort 7878)(destinations(list(sobj(port 8081)(paths(list \"/\")))(sobj(port 8080)(paths(list \"/service/\" \"/serve/\"))))))";
        String first = S$.car(sexp);
        assert first.equals("sobj");

        sexp = "((name \"David\"))";
        first = S$.car(sexp);
        assert first.equals("(name \"David\")");

        sexp = ("(name \"David Chen\")");
        first = S$.car(sexp);
        assert first.equals("name");

        sexp = ("((name))");
        first = S$.car(sexp);
        assert first.equals("(name)");

        sexp = ("(\"name\")");
        first = S$.car(sexp);
        assert first.equals("\"name\"");

        sexp = ("(\"na me\")");
        first = S$.car(sexp);
        assert first.equals("\"na me\"");

        sexp = ("((\"na me\"))");
        first = S$.car(sexp);
        assert first.equals("(\"na me\")");

        sexp = ("('('name))");
        first = S$.car(sexp);
        assert first.equals("'('name)");

        sexp = ("('('name)(a b))");
        first = S$.car(sexp);
        assert first.equals("'('name)");

        sexp = "(*list\"Shopping\"Ya.\"\"Running\"\"Football\")";
        first = S$.car(sexp);
        assert first.equals("*list");

        sexp = "(1\"hello\"\"world\")";
        first = S$.car(sexp);
        assert first.equals("1");

        sexp = "(*list \"Shopping\" \"Running\" \"Football\")";
        first = S$.car(sexp);
        assert first.equals("*list");

        sexp = "(\"Shop\\\"ping\" \"Run \\\" ning\" \"Football\")";
        first = S$.car(sexp);
        assert first.equals("\"Shop\\\"ping\"");

        sexp = "('a 'b 'c)";
        first = S$.car(sexp);
        assert first.equals("'a");

        sexp = "('e)";
        first = S$.car(sexp);
        assert first.equals("'e");

        sexp = "()";
        first = S$.car(sexp);
        assert first.isEmpty();
    }

    @Test
    public void cdrTest() {
        String sexp = "(sobj(listenPort 7878)(destinations(list(sobj(port 8081)(paths(list \"/\")))(sobj(port 8080)(paths(list \"/service/\" \"/serve/\"))))))";
        String left = S$.cdr(sexp);
        assert left.equals("((listenPort 7878)(destinations(list(sobj(port 8081)(paths(list \"/\")))(sobj(port 8080)(paths(list \"/service/\" \"/serve/\"))))))");

        sexp = ("((a b c)(name \"DavidChen\"))");
        left = S$.cdr(sexp);
        assert left.equals("((name \"DavidChen\"))");

        sexp = ("((name \"DavidChen\"))");
        left = S$.cdr(sexp);
        assert left.equals("()");

        sexp = ("(name \"DavidChen\")");
        left = S$.cdr(sexp);
        assert left.equals("(\"DavidChen\")");

        sexp = "(*list \"Shop\\\"ping\" \"Run \\\" ning\" \"Football\")";
        left = S$.cdr(sexp);
        assert left.equals("(\"Shop\\\"ping\" \"Run \\\" ning\" \"Football\")");
        left = S$.cdr(left);
        assert left.equals("(\"Run \\\" ning\" \"Football\")");

        sexp = "(1\"hello\"\"world\")";
        left = "(\"hello\"\"world\")";
        assert S$.cdr(sexp).equals(left);

        sexp = "()";
        left = S$.cdr(sexp);
        assert left.equals(SObjParser.NULL);

        sexp = "(a)";
        left = S$.cdr(sexp);
        assert left.equals(SObjParser.NULL);
    }
}
