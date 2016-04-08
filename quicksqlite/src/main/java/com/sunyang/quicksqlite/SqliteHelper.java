package com.sunyang.quicksqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sunyang.quicksqlite.Bulider.Query;
import com.sunyang.quicksqlite.Bulider.Update;
import com.sunyang.quicksqlite.Table.Table;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 这个库的目的是提供一种快捷操作android上的sqlite数据库的手段。
 *
 * 一般来说，操作数据库都是先在数据库中建立表，然后再操作表。但是移动端需求变化快，频繁的调整会导致维护表很麻烦。当业务复杂
 * 度上升的时候，维护难度也在上升，因此在设计这个库的时候我采用了Entity framework的Code First思想。即在代码运行时定义表结构而
 * 不是先在数据库里面预先定义好表结构。
 *
 *
 *
 */
public class SqliteHelper {
    private SQLiteDatabase mSqlliteDB;
    private final String mDBName = "_qs_database";
    private ArrayList<Table> mTables = new ArrayList<>();
    private Interpreter interpreter = new Interpreter();
    private static final SqliteHelper mSqliteHelpler = new SqliteHelper();

    public static SqliteHelper getInstance() {
        return mSqliteHelpler;
    }

    public void init(Context context) {
        mSqlliteDB = context.openOrCreateDatabase(mDBName, Context.MODE_PRIVATE, null);
    }

    public SQLiteDatabase getSqliteDatabase() {
        if (mSqlliteDB == null)
            throw new RuntimeException("SqliteHelper no init");
        return mSqlliteDB;
    }

    public void set(Object data) {
        if (mSqlliteDB == null)
            throw new RuntimeException("SqliteHelper no init");
        insert(initTable(data.getClass()), data);
    }

    public void clean(Class table) {
        if (mSqlliteDB == null)
            throw new RuntimeException("SqliteHelper no init");
        initTable(table);
        cleanTable(table);
        mSqlliteDB.execSQL(interpreter.getCleanSql(convertToTable(table)));
    }

    public <T> ArrayList<T> get(Class<T> clazz, Query query) {
        if (mSqlliteDB == null)
            throw new RuntimeException("SqliteHelper no init");
        initTable(clazz);
        return query(query, clazz, convertToTable(clazz));
    }

    public void delete(Class clazz, Query query) {
        if (mSqlliteDB == null)
            throw new RuntimeException("SqliteHelper no init");
        initTable(clazz);
        Log.e("update", interpreter.getDeleteSql(convertToTable(clazz), query == null ? null : query.toSql(clazz)));
        mSqlliteDB.execSQL(interpreter.getDeleteSql(convertToTable(clazz), query == null ? null : query.toSql(clazz)));
    }

    public void update(Class clazz, Update update, Query query) {
        if (mSqlliteDB == null)
            throw new RuntimeException("SqliteHelper no init");
        initTable(clazz);
        Log.e("update", interpreter.getUpdateSql(convertToTable(clazz), update.toSql(), query == null ? null : query.toSql(clazz)));
        mSqlliteDB.execSQL(interpreter.getUpdateSql(convertToTable(clazz), update.toSql(), query == null ? null : query.toSql(clazz)));
    }

    public void update(Object data, Query query) {
        if (mSqlliteDB == null)
            throw new RuntimeException("SqliteHelper no init");
        initTable(data.getClass());
        try {
            Log.e("update", interpreter.getUpdateSql(convertToTable(data.getClass()), data, query == null ? null : query.toSql(data.getClass())));
            mSqlliteDB.execSQL(interpreter.getUpdateSql(convertToTable(data.getClass()), data, query == null ? null : query.toSql(data.getClass())));
        } catch (NoSuchFieldException e) {
            rebuildTable(data.getClass());
            update(data, query);
        }
    }

    private void rebuildTable(Class table) {
        clean(table);
        initTable(table);
    }

    private Table create(Table table) {
        Log.e("create", interpreter.getCreateTableSql(table));
        mSqlliteDB.execSQL(interpreter.getCreateTableSql(table));
        return table;
    }

    private void insert(Table table, Object data) {
        try {
            Log.e("insert", interpreter.getInsertSql(table, data));
            mSqlliteDB.execSQL(interpreter.getInsertSql(table, data));
        } catch (NoSuchFieldException e) {
            rebuildTable(data.getClass());
            insert(table, data);
        }
    }

    private <T> ArrayList<T> query(Query query, Class<T> clazz, Table table) {
        ArrayList<T> result = new ArrayList<T>();
        try (Cursor c = mSqlliteDB.rawQuery(interpreter.getQuerySql(table, query == null ? null : query.toSql(clazz)), null)) {
            while (c.moveToNext()) {
                T temp = (T) Class.forName(clazz.getName()).newInstance();
                for (Object o : table.getFields().entrySet()) {
                    Map.Entry entry = (Map.Entry) o;
                    String key = (String) entry.getKey();
                    String value = (String) entry.getValue();
                    Field field = clazz.getDeclaredField(key);
                    field.setAccessible(true);
                    switch (value.toLowerCase()) {
                        case "integer":
                        case "byte":
                        case "short":
                        case "int":
                            field.set(temp, c.getInt(c.getColumnIndex(key)));
                            break;
                        case "long":
                            field.set(temp, c.getLong(c.getColumnIndex(key)));
                            break;
                        case "double":
                        case "float":
                            field.set(temp, c.getDouble(c.getColumnIndex(key)));
                            break;
                        case "char":
                        case "string":
                        default:
                            field.set(temp, c.getString(c.getColumnIndex(key)));
                            break;
                    }
                }
                result.add(temp);
            }
        } catch (NoSuchFieldException e) {
            rebuildTable(clazz);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("table not found");
        }catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

    private Table initTable(Class clazz) {
        String tableName = clazz.getName().replace(".", "_").toLowerCase();
        Table table = getTable(tableName);
        if (table == null) {
            table = create(convertToTable(clazz));
            mTables.add(table);
        }
        return table;
    }

    private void cleanTable(Class clazz) {
        String tableName = clazz.getName().replace(".", "_").toLowerCase();
        Table table = getTable(tableName);
        if (table != null) {
            mTables.remove(table);
        }
    }

    private Table convertToTable(Class clazz) {
        if (clazz == null)
            throw new NullPointerException();
        Table table = new Table();
        HashMap<String, String> fields = new HashMap<>();
        table.setTableName(clazz.getName().replace(".", "_").toLowerCase());

        for (Field f : clazz.getDeclaredFields()) {
            f.setAccessible(true);
            fields.put(f.getName(), f.getType().getSimpleName());
        }
        table.setFields(fields);
        return table;
    }

    private Table getTable(String tableName) {
        for (Table item : mTables) {
            if (item.getTableName().equals(tableName)) {
                return item;
            }
        }
        return null;
    }
}
