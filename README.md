# SObj in Java Programming language
`SObj` is a serial-data-type like `JSON`.
This is the Java Language version.

JavaScript version is also here:
https://github.com/AiziChen/SObj-typescript.git


### Compare between SObj & JSON
> SObj
```scheme
(*obj
  (id 1)  ;; user-id
  (name "DavidChen")
  (age 25)
  (birth "2019-01-16 01:08,30")
  (glasses (*obj
    (id 1)
    (degree 203.3)
    (color "RED-BLACK")))
  (height 167.3)
  ;; goods list/array
  (goods (*list
    (*obj (name "火龙果") (price 2.3))
    (*obj (name "雪梨") (price 3.2))))
  (behaviors (*list "Shopping""Running""Football")))
```
> JSON
```json
{
  "id" : 1,
  "name" : "DavidChen",
  "age" : 25,
  "birth" : 1547630304332,
  "glasses" : {
    "id" : 1,
    "degree" : 203.3,
    "color" : "RED-BLACK"
  },
  "height" : 167.3,
  "goods" : [ {
    "name" : "火龙果",
    "price" : 2.3
  }, {
    "name" : "雪梨",
    "price" : 3.2
  } ],
  "behaviors" : [ "Shopping", "Running", "Football" ]
}
```


### SObj's Object
> Normal SObj
```scheme
(*obj
  (id 1)
  (name "DavidChen")
  (age 25)
  (birth "2020-09-25 22:55,18")
  (glasses (*obj
    (id 1)
    (degree 203.3)
    (color "RED \"And\" BLACK")))
  (height 167.3)
  (goods (*list
    (*obj (name "火龙果")
      (price 2.3)
      (isVegetable #f))
    (*obj (name "雪梨")
      (price 3.2)
      (isVegetable #f))
    (*obj (name "西红柿")
      (price 2.5)
      (isVegetable #t))))
    (behaviors (*list "Shopping" "Running" "Football")))
```
> Array-Based SObj
```scheme
(*list
  (*obj (name "火龙果")
    (price 2.3)
    (isVegetable #f))
  (*obj (name "雪梨")
    (price 3.2)
    (isVegetable #f))
  (*obj (name "西红柿")
    (price 2.5)
    (isVegetable #t)))
```
> Primitive SObj

Support for the primitive datatypes of string, integer, long, double, float, char, boolean, byte and short.


### Performance Test - In (Intel I3 8300, JDK 11.0.8)
```shell
(*obj(id 1)(name "DavidChen")(age 25)(birth "2020-10-15 22:18,10")(glasses (*obj(id 1)(degree 203.3)(color "RED \"And\" BLACK")))(height 167.3)(goods (*list(*obj(name "火龙果")(price 2.3)(isVegetable #f))(*obj(name "雪梨")(price 3.2)(isVegetable #f))(*obj(name "西红柿")(price 2.5)(isVegetable #t))))(behaviors (*list"Shopping""Running""Football")))
>> From 9999 SObj total time: 293ms <<
--------------------------------------------
User{id=1, name='DavidChen', age=25, birth=Thu Sep 24 09:50:07 CST 2020, glasses=Glasses{id=1, degree=203.3, color=RED-BLACK}, height=167.3, goods=[Goods{name='火龙果', price=2.3, isVegetable=false}, Goods{name='雪梨', price=3.2, isVegetable=false}, Goods{name='西红柿', price=2.5, isVegetable=true}], behaviors=[Shopping, Running, Football]}
>> Parse 9999 objects total time: 969ms <<
```


## Syntax
* `(*obj ...)`  - Object SObj
* `(key value)` - Key-Value Pair SObj
* `(*list ...)` - Array SObj
* primitives-data-type - Value SObj


## Usage Example
> 1.Basic Usage.
```java
import org.quanye.sobj.SObjParser;
import xxx.xxx.User;

// 1st: Init your POJO or Record Instance
User user = new User(xxx, yyy, ....);
// then: Serialize Java Object to SObj
String sUser = SObjParser.fromObject(user);
// then: DeSerialize SObj to Java Object
User user = SObjParser.toObject(sUser, User.class);
```
> 2.SObjNode Usage.
```java
SObjNode rootNode = SObjParser.getRootNode(sUser);
// sample 1: get the User's name
SObjNode nameNode = rootNode.getNode("name");
if (sUser != null) {
    String name = nameNode.getValue(String.class);
}
// sample 2: get the User's glasses
SObjNode glassesNode = rootNode.getNode("glasses");
if (glassesNode != null) {
    Glasses glasses = glassesNode.getValue(Glasses.class);
    // then, use can use this glasses Object below
    System.out.println(glasses.getDegree());
}
// sampel 3: index the Goods array
SObjNode goodsNode = rootNode.getNode("goods");
// index first value of the `goods`:
Goods firstGood = goodsNode.getValue(0, Goods.class);
// index second value of the `goods`:
Goods secondGood = goodsNode.getValue(1, Goods.class);
// index third value of the `goods`:
Goods thirdGood = goodsNode.getValue(2, Goods.class);
// out of index indexing will produce `null`:
Goods nullGood = goodsNode.getValue(112, Goods.class);
```
> 3.SObj Override
```java
User defaultU1 = new User(yyy, zzz, xxx, ....);
String userDefinedSObj = "(*obj(id 2)(uid 0)(name \"Quanyec\")(age 26)(birth \"1995-09-24 09:50,07\")(glasses (*obj(price 115.5)(id 1)(degree 103.3)(color \"YELLOW-PURPLE\")))(height 167.3))";
User userDefinedU1 = SObjParser.toObject(userDefinedSObj, defaultU1);
assert userDefinedU1.eqauls(defaultU1);  // userDefinedU1 and defaultU1 is the same instance 
```
> 4.Parse SObj to JSON
```java
User u1 = new User(yyy, yyy, ....);
String u1SObj = SObjParser.fromObject(u1);
String u1JSON = STool.toJSON(u1SObj);
```
> 5.SObj Minimize
```java
User u1 = new User(xxx, yyy, ....);
String u1SObj = SObjParser.fromObject(u1);
String minimizeU1SObj = SObjParser.minimize(u1SObj);
```
> 6.SObj Beautify
```java
User u1 = new User(xxx, yyy, ....);
String u1SObj = SObjParser.fromObject(u1);
String beautifySObj = STool.beautify(u1SObj);
assert u1.toString().equals(SObjParser.toObject(beautifySObj, User.class).toString());
```
