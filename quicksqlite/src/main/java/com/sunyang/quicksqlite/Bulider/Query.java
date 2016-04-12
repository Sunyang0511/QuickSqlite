package com.sunyang.quicksqlite.Bulider;

import java.util.ArrayList;


public class Query {
    private ArrayList<Where> where = new ArrayList<>();
    private String[] opers = {"<", ">", "=", "!=", ">=", "<="};

    private Query() {
    }

    public static Query build() {
        return new Query();
    }

    private boolean operCan(String operator) {
        if (operator == null)
            throw new NullPointerException();
        for (String oper : opers) {
            if (operator.equals(oper))
                return true;
        }
        return false;
    }

    public Query filter(String key, String operator, Object value) {
        if (key == null || value == null)
            throw new NullPointerException();
        if (operCan(operator)) {
            Where where = new Where();
            where.setKey(key);
            where.setOper(operator);
            where.setValue(value);
            this.where.add(where);
        } else {
            throw new RuntimeException("This operator is not supported:" + operator);
        }
        return this;
    }

    public String toSql(Class clazz) {
        if (clazz == null)
            return null;
        StringBuffer sb = new StringBuffer();
        String tableName = clazz.getName().replace(".", "_").toLowerCase();
        for (Where item : where) {
            sb.append(tableName).append(".").append(item.getKey()).append(" ").append(item.getOper()).append(" ");
            switch (item.getValue().getClass().getSimpleName().toLowerCase()) {
                case "byte":
                case "short":
                case "int":
                case "long":
                case "double":
                case "float":
                case "integer":
                    sb.append(item.getValue()).append(" ");
                    break;
                case "char":
                case "string":
                default:
                    sb.append("'").append(item.getValue()).append("' ");
                    break;
            }
            if (where.indexOf(item) != where.size() - 1) {
                sb.append(" and ");
            }
        }
        return sb.toString().toLowerCase();
    }

    class Where {
        String key;
        String oper;
        Object value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getOper() {
            return oper;
        }

        public void setOper(String oper) {
            this.oper = oper;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }

}
