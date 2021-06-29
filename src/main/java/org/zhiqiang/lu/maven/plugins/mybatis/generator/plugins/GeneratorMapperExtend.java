package org.zhiqiang.lu.maven.plugins.mybatis.generator.plugins;

import freemarker.template.*;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

public class GeneratorMapperExtend extends PluginAdapter {
  public boolean validate(List<String> warnings) {
    return true;
  }

  public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
    System.out.println(JSON.toJSONString(this.getContext()));
    String javaMapperTargetPackage = this.getContext().getJavaClientGeneratorConfiguration().getTargetPackage();
    String javaMapperType = introspectedTable.getMyBatis3JavaMapperType();
    String javaClassName = javaMapperType.substring(javaMapperType.lastIndexOf('.') + 1, javaMapperType.length())
        .replace("Mapper", "");
    String targetProject = this.getContext().getJavaClientGeneratorConfiguration().getTargetProject();
    String javaClientTargetPackage = this.getContext().getJavaClientGeneratorConfiguration().getTargetPackage();

    Map<String, String> root = new HashMap<String, String>();
    root.put("javaMapperTargetPackage", javaMapperTargetPackage);
    root.put("javaClientTargetPackage", javaClientTargetPackage);
    root.put("EntityName", javaClassName);
    genMapperExtend(targetProject, javaMapperTargetPackage, javaClassName, root);
    return super.contextGenerateAdditionalJavaFiles(introspectedTable);
  }

  private void genMapperExtend(String targetProject, String javaMapperTargetPackage, String javaClassName,
      Map<String, String> root) {
    String dirPath = targetProject + "/" + javaMapperTargetPackage.replaceAll("\\.", "/") + "/extend";
    String filePath = dirPath + "/" + javaClassName + "MapperExtend.java";
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
    try {
      Template temp = cfg.getTemplate("mybatis/generator/mapper_extend.ftl");
      Writer out = new OutputStreamWriter(new FileOutputStream(file));
      temp.process(root, out);
      out.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("Saving file " + javaClassName + "MapperExtend.java");
  }
}
