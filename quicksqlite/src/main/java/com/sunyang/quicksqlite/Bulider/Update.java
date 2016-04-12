package com.sunyang.quicksqlite.Bulider;

import java.util.ArrayList;

public class Update {

    private ArrayList<Set> setList = new ArrayList<>();

    private Update() {
    }

    public static Update build() {
        return new Update();
    }

    public Update update(String key, Object value) {
        if (key == null || value == null)
            throw new NullPointerException();
        Set where = new Set();
        where.setKey(key);
        where.setValue(value);
        this.setList.add(where);
        return this;
    }

    public String toSql() {
        if (setList == null || setList.size() == 0)
            return null;
        StringBuffer sb = new StringBuffer();
        for (Set item : setList) {
            sb.append(item.getKey()).append("=");
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
            if (setList.indexOf(item) != setList.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString().toLowerCase();
    }

    private class Set {
        String key;
        Object value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
}
