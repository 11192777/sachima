package org.zaizai.sachima.sql.adapter.bean;

import org.zaizai.sachima.sql.ast.statement.SQLColumnDefinition;
import org.zaizai.sachima.util.StringUtils;

public class TabColumn {
    private String tableName;
    private String columnName;
    private String dataType;
    private Boolean nonNull;
    private String defaultValue;

    public TabColumn() {
    }

    public TabColumn(String tableName, String columnName) {
        this.tableName = tableName;
        this.columnName = columnName;
    }

    public TabColumn(String tableName, String columnName, String dataType) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public TabColumn(String tableName, SQLColumnDefinition sqlColumnDefinition) {
        this.tableName = tableName;
        this.columnName = sqlColumnDefinition.getColumnName();
        this.dataType = sqlColumnDefinition.getDataType().getName();
    }

    public static TabColumn builder() {
        return new TabColumn();
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void setDataType(String dataType) {
        int leftBracketIndex = dataType.indexOf("(");
        this.dataType = leftBracketIndex == -1 ? dataType : dataType.substring(0, leftBracketIndex);
    }

    public void setNonNull(Boolean nonNull) {
        this.nonNull = nonNull;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return StringUtils.format("tableName:{}, columnName:{}, dataType:{}", this.tableName, this.columnName, this.dataType);
    }
}