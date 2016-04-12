# quicksqlite
  ORM Framework based on Android Sqlite,Generate data sheet from object

    基于android sqlite的ORM框架
    特点是由对象生成数据表

    采用对象式数据库访问方式及方法链式查询
    增删改查操作中均不用考虑数据表是否存在，框架会自主生产对应表
    如果对象字段有变化,则会自动删除旧表及数据，然后重新生成新表，例如修改对象字段后插入新对象，
    插入时库检测到字段不匹配，则会删除旧表及旧数据，然后生成新表并插入数据.

# Maven

    <dependency>
      <groupId>com.sunyang.quicksqlite</groupId>
      <artifactId>quicksqlite</artifactId>
      <version>1.0.0</version>
      <type>pom</type>
    </dependency>

# Gradle

    dependencies {
        compile 'com.sunyang.quicksqlite:quicksqlite:1.0.0'
    }   


简单示例：
===

    insert:
        TestModel test = new TestModel();
        test.setField1("测试字段1");
        test.setField2(2);
        test.setField3(3D);
        SqliteHelper.getInstance().set(test);//数据库中会自动生成包含TestModel中所有字段的数据表
        
    query:
        //使用Query查询链生成查询,不用考虑表是否存在,没有异常,不会报错,只要做好null值处理即可.
        List<TestModel> temp = SqliteHelper.getInstance().get(TestModel.class, Query.build().filter("field2", "=", 2));
        if (temp != null && temp.size() > 0)
            tv.setText(temp.get(0).getField1());
    
    update:
        TestModel test = new TestModel();
        test.setField1("字段全部修改了");
        test.setField2(2);
        test.setField3(26D);

        //全量更新模式,所有字段均会被更新到数据库中
        SqliteHelper.getInstance().update(test, Query.build().filter("field2", "=", 2));
        //指定更新模式,使用Update类指定需要更新字段
        SqliteHelper.getInstance().update(TestModel.class, Update.build().update("field1","更新指定字段"),Query.build().filter                  ("field2","=",2));
        
    delete:
        //删除指定表指定字段,query传null则清空表数据
        SqliteHelper.getInstance().delete(TestModel.class, Query.build().filter("field2", "=", 2));
    

tips: 目前只能针对非符合型对象生成表,即对象的字段均为基本类型,例如:
    
    public class TestModel {

    private String field1;
    private int field2;
    private double field3;

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public int getField2() {
        return field2;
    }

    public void setField2(int field2) {
        this.field2 = field2;
    }

    public double getField3() {
        return field3;
    }

    public void setField3(double field3) {
        this.field3 = field3;
    }
}

而类似以下对象则不能支持:
public class TestModel {

    private String field1;
    private int field2;
    private double field3;
    private error errorObject;

    public error getErrorObject() {
        return errorObject;
    }

    public void setErrorObject(error errorObject) {
        this.errorObject = errorObject;
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public int getField2() {
        return field2;
    }

    public void setField2(int field2) {
        this.field2 = field2;
    }

    public double getField3() {
        return field3;
    }

    public void setField3(double field3) {
        this.field3 = field3;
    }

    class error{
        private String error;

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}

