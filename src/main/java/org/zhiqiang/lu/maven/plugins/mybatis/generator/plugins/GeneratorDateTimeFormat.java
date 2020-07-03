package org.zhiqiang.lu.maven.plugins.mybatis.generator.plugins;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;


public class GeneratorDateTimeFormat extends PluginAdapter {
    public GeneratorDateTimeFormat() {
    }

    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addImportedType("com.fasterxml.jackson.annotation.JsonFormat");
        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        String date = this.properties.getProperty("dateFormat");
        String time = this.properties.getProperty("timeFormat");
        if (null == date) {
            date = "yyyy-MM-dd";
        }
        if (null == time) {
            time = "yyyy-MM-dd HH:mm:ss";
        }
        if (introspectedColumn.getJdbcType() == 93) {
            field.addAnnotation("@JsonFormat(timezone = \"GMT+8\", pattern = \"" + time + "\")");
        } else if (introspectedColumn.getJdbcType() == 91) {
            field.addAnnotation("@JsonFormat(timezone = \"GMT+8\", pattern = \"" + date + "\")");
        }
        return super.modelFieldGenerated(field, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }
}
