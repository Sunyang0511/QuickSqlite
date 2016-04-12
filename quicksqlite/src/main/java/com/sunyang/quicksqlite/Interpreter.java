package com.sunyang.quicksqlite;

import com.sunyang.quicksqlite.Table.Table;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;


public class Interpreter {

    public String getInsertSql(Table table, Object data) throws NoSuchFieldException {
        if (table == null || table.getFields() == null || table.getFields().size() < 1 || data == null)
            return null;
        StringBuffer sb = new StringBuffer("INSERT INTO ");
        sb.append(table.getTableName());
        StringBuffer fieldSb = new StringBuffer();
        StringBuffer valueSb = new StringBuffer();

        for (Object o : table.getFields().entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            String key = entry.getKey().toString().toLowerCase();
            String type = entry.getValue().toString().toLowerCase();
            Field field = data.getClass().getDeclaredField(key);
            field.setAccessible(true);
            Object value = null;
            try {
                value = field.get(data);
            } catch (IllegalAccessException e) {
                continue;
            }

            switch (type) {
                case "byte":
                case "short":
                case "int":
                case "long":
                case "double":
                case "float":
                case "integer":
                    valueSb.append(value).append(",");
                    break;
                case "char":
                case "string":
                default:
                    if (value != null)
                        valueSb.append("'").append((String) value).append("',");
                    else
                        valueSb.append("'',");
                    break;
            }
            fieldSb.append(key).append(",");
        }

        return sb.append("(").append(fieldSb.substring(0, fieldSb.length() - 1)).append(") VALUES(")
                .append(valueSb.substring(0, valueSb.length() - 1)).append(")").toString().toLowerCase();
    }

    public String getCreateTableSql(Table table) {
        if (table.getFields() != null && table.getFields().size() < 1)
            return null;
        StringBuffer sb = new StringBuffer("create table if not exists ");
        sb.append(table.getTableName()).append("(id INTEGER PRIMARY KEY,");

        Iterator iter = table.getFields().entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = entry.getKey().toString().toLowerCase();
            String value = entry.getValue().toString().toLowerCase();

            switch (value) {
                case "int":
                case "long":
                    sb.append(key).append(" ").append("INTEGER,");
                    break;
                case "double":
                case "float":
                    sb.append(key).append(" ").append("REAL,");
                    break;
                case "string":
                    sb.append(key).append(" ").append("TEXT,");
                    break;
            }
        }
        return sb.substring(0, sb.length() - 1) + ")";
    }

    public String getQuerySql(Table table, String where) {
        StringBuffer sb = new StringBuffer("SELECT ");
        for (Object o : table.getFields().entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            String key = entry.getKey().toString().toLowerCase();
            sb.append(table.getTableName()).append(".").append(key).append(",");
        }
        if (where == null)
            return sb.substring(0, sb.length() - 1) + " from " + table.getTableName();
        else
            return sb.substring(0, sb.length() - 1) + " from " + table.getTableName() + " where " + where;
    }

    public String getUpdateSql(Table table, String update, String where) {
        if (table == null)
            throw new NullPointerException();
        StringBuffer sb = new StringBuffer("UPDATE ");
        sb.append(table.getTableName());
        sb.append(" SET ");
        sb.append(update);
        if (where != null) {
            sb.append(" WHERE ");
            sb.append(where);
        }
        return sb.toString();
    }

    public String getUpdateSql(Table table, Object data, String where) throws NoSuchFieldException {
        if (table == null || table.getFields() == null || table.getFields().size() < 1 || data == null)
            return null;
        StringBuffer sb = new StringBuffer("UPDATE ");
        sb.append(table.getTableName());
        sb.append(" SET ");

        for (Object o : table.getFields().entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            String key = entry.getKey().toString().toLowerCase();
            String type = entry.getValue().toString().toLowerCase();
            Field field = data.getClass().getDeclaredField(key);
            field.setAccessible(true);
            Object value = null;
            try {
                value = field.get(data);
            } catch (IllegalAccessException e) {
                continue;
            }

            sb.append(key).append("=");
            switch (type) {
                case "byte":
                case "short":
                case "int":
                case "long":
                case "double":
                case "float":
                case "integer":
                    sb.append(value).append(",");
                    break;
                case "char":
                case "string":
                default:
                    if (value != null)
                        sb.append("'").append((String) value).append("',");
                    else
                        sb.append("'',");
                    break;
            }
        }

        if (where == null)
            return sb.substring(0, sb.length() - 1);
        else
            return sb.substring(0, sb.length() - 1) + " where " + where;
    }

    public String getDeleteSql(Table table, String where) {
        if (table == null)
            throw new NullPointerException();

        StringBuffer sb = new StringBuffer("DELETE FROM ");
        sb.append(table.getTableName());
        if (where != null) {
            sb.append(" WHERE ");
            sb.append(where);
        }
        return sb.toString();
    }

    public String getCleanSql(Table table) {
        if (table == null)
            throw new NullPointerException();
        return "DROP TABLE  " + table.getTableName();
    }

}
