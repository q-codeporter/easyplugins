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

    public void setActualColumnName(final String actualColumnName) {
        this.actualColumnName = actualColumnName;
    }

    public String getActualTypeName() {
        return actualTypeName;
    }

    public void setActualTypeName(final String actualTypeName) {
        this.actualTypeName = actualTypeName;
    }

    public int getLength() {
        return length;
    }

    public void setLength(final int length) {
        this.length = length;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(final String remarks) {
        this.remarks = remarks;
    }

    public FullyQualifiedJavaType getFullyQualifiedJavaType() {
        return fullyQualifiedJavaType;
    }

    public void setFullyQualifiedJavaType(final FullyQualifiedJavaType fullyQualifiedJavaType) {
        this.fullyQualifiedJavaType = fullyQualifiedJavaType;
    }
}
