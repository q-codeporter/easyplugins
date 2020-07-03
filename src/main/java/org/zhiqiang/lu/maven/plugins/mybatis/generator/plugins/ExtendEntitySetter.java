package org.zhiqiang.lu.maven.plugins.mybatis.generator.plugins;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

public class ExtendEntitySetter extends PluginAdapter {
    public ExtendEntitySetter() {
    }

    public boolean validate(List<String> list) {
        return true;
    }

    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        method.setReturnType(topLevelClass.getType());
        method.addBodyLine("return this;");
        return super.modelSetterMethodGenerated(method, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }
}