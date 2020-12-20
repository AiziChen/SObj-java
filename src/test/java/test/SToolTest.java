package test;

import domain.Glasses;
import domain.Goods;
import org.junit.Test;
import org.quanye.sobj.SObjParser;
import org.quanye.sobj.STool;
import org.quanye.sobj.datatype.SObjTable;
import org.quanye.sobj.exception.InvalidSObjSyntaxException;
import domain.User;
import org.quanye.sobj.tools.C$;

public class SToolTest {
    @Test
    public void toSObjTableTest() throws InvalidSObjSyntaxException {
        User u1 = BaseTest.getU1();
        String u1SObj = SObjParser.fromObject(u1);
        SObjTable<Object, Object> st = STool.toSObjTable(u1SObj);

        Integer id = st.getValue("id", Integer.class);
        assert id.equals(u1.getId());
        String name = st.getValue("name", String.class);
        assert name.equals(u1.getName());

        Double glassDegree = st.getValue("glasses", Glasses.class).getDegree();
        assert glassDegree.toString().equals(u1.getGlasses().getDegree().toString());

        SObjTable<Object, Object> sn = st.getNode("goods");
        assert sn.getValue(0, Goods.class).toString()
                .equals(u1.getGoods()[0].toString());
        assert sn.getValue(1, Goods.class).toString()
                .equals(u1.getGoods()[1].toString());
        assert sn.getValue(2, Goods.class).toString()
                .equals(u1.getGoods()[2].toString());
        assert sn.listIndex(3) == null;

        sn = st.getNode("behaviors");
        assert sn.getValue(0, String.class)
                .equals(u1.getBehaviors()[0]);
        assert sn.getValue(1, String.class)
                .equals(u1.getBehaviors()[1]);
        assert sn.getValue(2, String.class)
                .equals(u1.getBehaviors()[2]);
        assert sn.listIndex(3) == null;
    }

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
