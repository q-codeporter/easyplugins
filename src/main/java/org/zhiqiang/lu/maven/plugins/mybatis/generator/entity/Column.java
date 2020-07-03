package org.zhiqiang.lu.maven.plugins.mybatis.generator.entity;

public class Column {
    private String actualColumnName;

    private String actualTypeName;

    private int length;

    private String remarks;

    private FullyQualifiedJavaType fullyQualifiedJavaType;

    public String getActualColumnName() {
        return actualColumnName;
    }

    public void setActualColumnName(String actualColumnName) {
        this.actualColumnName = actualColumnName;
    }

    public String getActualTypeName() {
        return actualTypeName;
    }

    public void setActualTypeName(String actualTypeName) {
        this.actualTypeName = actualTypeName;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public FullyQualifiedJavaType getFullyQualifiedJavaType() {
        return fullyQualifiedJavaType;
    }

    public void setFullyQualifiedJavaType(FullyQualifiedJavaType fullyQualifiedJavaType) {
        this.fullyQualifiedJavaType = fullyQualifiedJavaType;
    }
}
