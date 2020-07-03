package org.zhiqiang.lu.maven.plugins.mybatis.generator.plugins;

import freemarker.template.*;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneratorServices extends PluginAdapter {
    public boolean validate(List<String> warnings) {
        return true;
    }

    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        String javaRepositoryPackage = this.getContext().getJavaClientGeneratorConfiguration().getTargetPackage();
        String javaMapperType = introspectedTable.getMyBatis3JavaMapperType();
        String topPackage = javaRepositoryPackage.substring(0, javaRepositoryPackage.lastIndexOf('.'));
        String javaClassName = javaMapperType.substring(javaMapperType.lastIndexOf('.') + 1, javaMapperType.length())
                .replace("Mapper", "");
        String targetProject = this.getContext().getJavaClientGeneratorConfiguration().getTargetProject();
        String javaModelTargetPackage = this.getContext().getJavaModelGeneratorConfiguration().getTargetPackage();
        String javaClientTargetPackage = this.getContext().getJavaClientGeneratorConfiguration().getTargetPackage();

        Map<String, String> root = new HashMap<String, String>();
        root.put("topPackage", topPackage);
        root.put("javaModelTargetPackage", javaModelTargetPackage);
        root.put("javaClientTargetPackage", javaClientTargetPackage);
        root.put("EntityName", javaClassName);
        root.put("entityName", new StringBuilder().append(Character.toLowerCase(javaClassName.charAt(0)))
                .append(javaClassName.substring(1)).toString());
        genService(targetProject, topPackage, javaClassName, root);
        return super.contextGenerateAdditionalJavaFiles(introspectedTable);
    }

    private void genService(String targetProject, String topPackage, String javaClassName, Map<String, String> root) {
        String dirPath = targetProject + "/" + topPackage.replaceAll("\\.", "/") + "/service";
        String filePath = dirPath + "/" + javaClassName + "Service.java";
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
            Template temp = cfg.getTemplate("mybatis/generator/service.ftl");
            Writer out = new OutputStreamWriter(new FileOutputStream(file));
            temp.process(root, out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Saving file " + javaClassName + "Service.java");
    }
}
