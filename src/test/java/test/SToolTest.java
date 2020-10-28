package test;

import org.junit.Test;
import org.quanye.sobj.SObjParser;
import org.quanye.sobj.STool;
import org.quanye.sobj.exception.InvalidSObjSyntaxException;
import domain.User;

public class SToolTest {
    @Test
    public void toJsonTest() throws InvalidSObjSyntaxException {
        String u1SObj = SObjParser.fromObject(BaseTest.getU1());
        String u1JSON = STool.toJSON(u1SObj);
        System.out.println("=====toJsonTest Result======\n" + u1JSON);
    }

    @Test
    public void minimizeTest() throws InvalidSObjSyntaxException {
        User u1 = BaseTest.getU1();
        String u1SObj = SObjParser.fromObject(u1);
        String minimizeU1SObj = STool.minimize(u1SObj);
        System.out.println("=====minimized test result=====\n" + minimizeU1SObj);

        User tmpU1 = SObjParser.toObject(minimizeU1SObj, User.class);
        assert u1.toString().equals(tmpU1.toString());
    }

    @Test
    public void beautifyTest() throws InvalidSObjSyntaxException {
        User u1 = BaseTest.getU1();
        String u1SObj = SObjParser.fromObject(u1);
        String beautifySObj = STool.beautify(u1SObj);
        assert u1.toString().equals(SObjParser.toObject(beautifySObj, User.class).toString());
        System.out.println("=====beautify test result=====\n" + beautifySObj);
    }
}
