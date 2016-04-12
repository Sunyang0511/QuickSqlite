package com.sunyang.quicksqlite.Table;

import java.util.HashMap;

public final class Table {
    private String tableName = "";
    private HashMap<String, String> fields;

    public String getTableName() {
        return tableName.toLowerCase();
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public HashMap<String, String> getFields() {
        return fields;
    }

    public void setFields(HashMap<String, String> fields) {
        this.fields = fields;
    }
}
