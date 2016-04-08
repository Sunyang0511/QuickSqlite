package com.sunyang.quickexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sunyang.quicksqlite.Bulider.Query;
import com.sunyang.quicksqlite.Bulider.Update;
import com.sunyang.quicksqlite.SqliteHelper;

import java.util.List;

public class ExampleActivity extends AppCompatActivity {

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        Button get = (Button) findViewById(R.id.btn1);
        Button set = (Button) findViewById(R.id.btn2);
        Button update = (Button) findViewById(R.id.btn3);
        final Button delete = (Button) findViewById(R.id.btn4);
        Button clean = (Button) findViewById(R.id.btn5);

        tv = (TextView) findViewById(R.id.tv);

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setData();
            }
        });
        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });
        clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clean();
            }
        });
        SqliteHelper.getInstance().init(this);
    }

    void clean() {
        SqliteHelper.getInstance().clean(TestModel.class);
    }

    void update() {
        TestModel test = new TestModel();
        test.setField1("字段全部修改了");
        test.setField2(2);
        test.setField3(26D);

        SqliteHelper.getInstance().update(test, null);
        SqliteHelper.getInstance().update(TestModel.class, Update.build().update("field1","更新指定字段"),Query.build().filter("field2","=",2));
    }

    void delete() {
        SqliteHelper.getInstance().delete(TestModel.class, null);
    }

    void setData() {
        TestModel test = new TestModel();
        test.setField1("测试字段1");
        test.setField2(2);
        test.setField3(3D);
        SqliteHelper.getInstance().set(test);
    }

    void getData() {
        List<TestModel> temp = SqliteHelper.getInstance().get(TestModel.class, Query.build().filter("field2", "=", 2));
        if (temp != null && temp.size() > 0)
            tv.setText(temp.get(0).getField1());
    }
}
