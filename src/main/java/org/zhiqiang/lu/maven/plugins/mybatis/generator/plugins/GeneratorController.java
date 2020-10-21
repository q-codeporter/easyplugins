package org.zhiqiang.lu.maven.plugins.mybatis.generator.plugins;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.config.TableConfiguration;
import org.zhiqiang.lu.maven.plugins.mybatis.generator.entity.Column;

import java.io.*;
import java.util.*;

public class GeneratorController extends PluginAdapter {
  public boolean validate(List<String> warnings) {
    return true;
  }

  @Override
  public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
    TableConfiguration t = introspectedTable.getTableConfiguration();
    if ("true".equals(t.getProperties().getProperty("controller"))) {
      String javaRepositoryPackage = this.getContext().getJavaClientGeneratorConfiguration().getTargetPackage();
      String javaMapperType = introspectedTable.getMyBatis3JavaMapperType();
      String topPackage = javaRepositoryPackage.substring(0, javaRepositoryPackage.lastIndexOf('.'));
      String javaClassName = javaMapperType.substring(javaMapperType.lastIndexOf('.') + 1, javaMapperType.length())
          .replace("Mapper", "");
      String targetProject = this.getContext().getJavaClientGeneratorConfiguration().getTargetProject();
      String javaModelTargetPackage = this.getContext().getJavaModelGeneratorConfiguration().getTargetPackage();
      String javaClientTargetPackage = this.getContext().getJavaClientGeneratorConfiguration().getTargetPackage();

      Map<String, Object> root = new HashMap<>();
      root.put("topPackage", topPackage);
      root.put("javaModelTargetPackage", javaModelTargetPackage);
      root.put("javaClientTargetPackage", javaClientTargetPackage);
      root.put("EntityName", javaClassName);
      root.put("entityName", new StringBuilder().append(Character.toLowerCase(javaClassName.charAt(0)))
          .append(javaClassName.substring(1)).toString());
      root.put("entity_name", root.get("entityName").toString().replaceAll("[A-Z]", "_$0").toLowerCase());
      root.put("remarks", introspectedTable.getRemarks());
      List<Column> primary_key = new ArrayList<>();
      Set<String> packages = new HashSet<>();
      for (GeneratedJavaFile g : introspectedTable.getPrimaryKeyColumns().get(0).getIntrospectedTable()
          .getGeneratedJavaFiles()) {
        List<JSONObject> methods = JSON.parseArray(
            JSON.parseObject(JSON.toJSONString(g.getCompilationUnit())).getString("methods"), JSONObject.class);
        for (JSONObject m : methods) {
          if ("selectByPrimaryKey".equals(m.getString("name"))) {
            List<JSONObject> parameters = JSON.parseArray(m.getString("parameters"), JSONObject.class);
            for (JSONObject parameter : parameters) {
              Column column = JSON.parseObject(
                  JSON.toJSONString(introspectedTable.getColumn(parameter.getString("name"))), Column.class);
              if (!"java.lang".equals(column.getFullyQualifiedJavaType().getPackageName())) {
                packages.add(column.getFullyQualifiedJavaType().getFullyQualifiedName());
              }
              primary_key.add(column);
            }
          }
        }
      }
      root.put("primary_key", primary_key);
      root.put("packages", packages);
      genController(targetProject, topPackage, javaClassName, root);
    }
    return super.contextGenerateAdditionalJavaFiles(introspectedTable);
  }

  private void genController(String targetProject, String topPackage, String javaClassName, Map<String, Object> root) {
    String dirPath = targetProject + "/" + topPackage.replaceAll("\\.", "/") + "/controller";
    String filePath = dirPath + "/" + javaClassName + "Controller.java";
    File dir = new File(dirPath);
    File file = new File(filePath);
    if (file.exists()) {
      System.err.println(file + " already exists, it was skipped.");
      return;
    } else {
      try {
        dir.mkdirs();
        file.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    Configuration cfg = new Configuration(Configuration.VERSION_2_3_30);
    cfg.setClassForTemplateLoading(this.getClass(), "/");
    cfg.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_30));
    cfg.setEncoding(Locale.CHINA, "UTF-8");
    try {
      Template temp;
      if (GeneratorSwagger2Doc.controller_swagger) {
        temp = cfg.getTemplate("mybatis/generator/controller_swagger.ftl", Locale.CHINA);
      } else {
        temp = cfg.getTemplate("mybatis/generator/controller.ftl", Locale.CHINA);
      }
      Writer out = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
      temp.process(root, out);
      out.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("Saving file " + javaClassName + "Controller.java");
  }
}
