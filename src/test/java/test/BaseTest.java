package test;

import org.junit.Test;
import org.quanye.sobj.SObjParser;
import org.quanye.sobj.exception.InvalidSObjSyntaxException;
import org.quanye.sobj.struct.SObjNode;
import domain.Glasses;
import domain.Goods;
import domain.User;

import java.util.Arrays;
import java.util.Date;

public class BaseTest {
    private final static Glasses glasses = new Glasses(1, 203.3, "RED \\\"And\\\" BLACK");
    private final static Goods[] goodss = {
            new Goods("火龙果", 2.3F, false),
            new Goods("雪梨", 3.2F, false),
            new Goods("西红柿", 2.5F, true)
    };
    private final static String[] behaviors = new String[]{"Shopping", "Running", "Football"};
    private final static User u1 = new User(1, "DavidChen", 25, new Date(814233600000L), glasses, 167.3, goodss, behaviors, null);

    public static User getU1() {
        return u1;
    }

    @Test
    public void fromObjectTest() {
        // Parse to Object
        String u1SObj = SObjParser.fromObject(u1);
        System.out.println("=====serialize Object Test Result=====\n" + u1SObj);

        // Parse to List Object
        String goods = SObjParser.fromObject(goodss);
        System.out.println("=====serialize Array-Object Test Result=====\n" + goods);
    }

    @Test
    public void fromObjectPerformanceTest() {
        // Check from 9999s user object
        long before = System.currentTimeMillis();
        for (int i = 0; i < 9999; ++i) {
            SObjParser.fromObject(u1);
        }
        long after = System.currentTimeMillis();
        System.out.println(">> From 9999 SObj total time: " + (after - before) + "ms <<");
    }


    @Test
    public void toObjectTest() throws InvalidSObjSyntaxException {
        String u1SObj = SObjParser.fromObject(u1);
        // Print the result object
        User result = SObjParser.toObject(u1SObj, User.class);
        System.out.println("=====deserialize to Object result=====\n"
                + "u1 = " + result);

        String goodsSobj = SObjParser.fromObject(goodss);
        Goods[] goods = SObjParser.toObject(goodsSobj, Goods[].class);
        System.out.println("=====deserialize to Array-Object result=====\n" + Arrays.toString(goods));
    }

    @Test
    public void toObjectPerformanceTest() throws InvalidSObjSyntaxException {
        String u1SObj = SObjParser.fromObject(u1);
        // Check parse 9999s user object
        long before = System.currentTimeMillis();
        for (int i = 0; i < 9999; ++i) {
            SObjParser.toObject(u1SObj, User.class);
        }
        long after = System.currentTimeMillis();
        System.out.println(">> Parse 9999 objects total time: " + (after - before) + "ms <<");
    }


    @Test
    public void lessVariableTest() throws InvalidSObjSyntaxException {
        String sobj1 = "(*obj(id 1)(uid 0)(name \"DavidChen\")(age 25)(birth \"1995-10-21 08:00,00\")(glasses (*obj(price 115.5)(id 1)(degree 203.3)(color \"RED-BLACK\")))(height 167.3))";
        // Converting successful required:
        User lessVariableUser = SObjParser.toObject(sobj1, User.class);
        System.out.println("=====less variable deserialize test result=====\n" + lessVariableUser);

        String u1SObj = SObjParser.fromObject(lessVariableUser);
        System.out.println("=====less variable serialize test result=====\n" + u1SObj);
    }


    @Test
    public void getSObjNodeTest() {
        String u1SObj = SObjParser.fromObject(u1);
        SObjNode node = SObjParser.getRootNode(u1SObj);
        String name = node.getNode("name").getValue(String.class);
        assert name.equals(u1.getName());
        Double glassDegree = node.getNode("glasses").getValue(Glasses.class).getDegree();
        assert glassDegree.equals(u1.getGlasses().getDegree());
    }


    @Test
    public void toObjectOverrideTest() throws InvalidSObjSyntaxException {
        String userDefinedSObj = "(*obj(id 2)(uid 0)(name \"Quanyec\")(age 26)(birth \"1995-10-21 08:00,00\")(glasses (*obj(price 115.5)(id 1)(degree 103.3)(color \"YELLOW-PURPLE\")))(height 167.3))";
        User defaultU1 = u1;
        User userDefinedU1 = SObjParser.toObject(userDefinedSObj, defaultU1);
        System.out.println("=====toObject Override Test Result=====\n" + userDefinedU1);
    }


    @Test
    public void nullValueTest() throws InvalidSObjSyntaxException {
        String nullTestObj = "(*obj(nill ())(id 1)(name \"DavidChen\")(age 25)(birth \"1995-10-21 08:00,00\")(glasses (*obj(id 1)(degree 203.3)(color \"RED \\\"And\\\" BLACK\")))(height 167.3)(goods (*list(*obj(name \"火龙果\")(price 2.3)(isVegetable #f))(*obj(name \"雪梨\")(price 3.2)(isVegetable #f))(*obj(name \"西红柿\")(price 2.5)(isVegetable #t))))(behaviors (*list\"Shopping\"\"Running\"\"Football\")))";
        User nullTestUser = SObjParser.toObject(nullTestObj, User.class);
        System.out.println("=====toObject contain the `null` value Result======\n" + nullTestUser);
        assert SObjParser.fromObject(u1).equals(SObjParser.fromObject(nullTestUser));
    }

}
