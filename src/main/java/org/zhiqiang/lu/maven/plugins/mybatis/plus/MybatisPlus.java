package org.zhiqiang.lu.maven.plugins.mybatis.plus;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

@Mojo(name = "mybatis-plus", defaultPhase = LifecyclePhase.PACKAGE)
public class MybatisPlus extends AbstractMojo {
    @Parameter
    private String configurationFile;

    public void execute() throws MojoExecutionException {
        // 项目路径
        String projectPath = System.getProperty("user.dir");

        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 读取配置文件
        Yaml yaml = new Yaml();
        JSONObject properties = new JSONObject();
        try {
            ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
            Object obj = yamlReader.readValue(yaml.dump(yaml.load(new BufferedInputStream(new FileInputStream(projectPath + "/" + configurationFile)))), Object.class);
            ObjectMapper jsonWriter = new ObjectMapper();
            properties = JSON.parseObject(jsonWriter.writeValueAsString(obj));
        } catch (IOException e) {
            System.out.println("加载配置问件异常");
            e.printStackTrace();
        }
        JSONObject global = properties.getJSONObject("easycode").getJSONObject("mybatis").getJSONObject("global");
        JSONObject datasource = properties.getJSONObject("easycode").getJSONObject("mybatis").getJSONObject("datasource");
        JSONObject application = properties.getJSONObject("easycode").getJSONObject("mybatis").getJSONObject("package");
        JSONObject template = properties.getJSONObject("easycode").getJSONObject("mybatis").getJSONObject("template");
        JSONObject strategy = properties.getJSONObject("easycode").getJSONObject("mybatis").getJSONObject("strategy");

        // 全局配置
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setOutputDir(projectPath + "/src/main/java");
        globalConfig.setAuthor("Q");
        globalConfig.setOpen(false);
        globalConfig.setSwagger2(global.getBoolean("swagger"));
        globalConfig.setServiceName("%sService");
        mpg.setGlobalConfig(globalConfig);

        // 数据源配置
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUrl(datasource.getString("url"));
        // dataSourceConfig.setSchemaName("public");
        dataSourceConfig.setDriverName(datasource.getString("driver-class-name"));// JDK8
        dataSourceConfig.setUsername(datasource.getString("username"));
        dataSourceConfig.setPassword(datasource.getString("password"));
        mpg.setDataSource(dataSourceConfig);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setModuleName(application.getString("name"));
        pc.setParent(application.getString("parent"));
        mpg.setPackageInfo(pc);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();

        // 配置自定义输出模板
        //指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
        // templateConfig.setEntity("templates/entity.java");
        // templateConfig.setService("templates/service.java");
        templateConfig.setController("mybatis/mybatis/controller.java");
        // templateConfig.setXml("templates/mapper.xml");
        mpg.setTemplate(templateConfig);

        // 策略配置
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig.setNaming(NamingStrategy.underline_to_camel);
        strategyConfig.setColumnNaming(NamingStrategy.underline_to_camel);
        //strategy.setSuperEntityClass("你自己的父类实体,没有就不用设置!");
        strategyConfig.setEntityLombokModel(strategy.getBoolean("lombok"));
        strategyConfig.setRestControllerStyle(true);
        // 公共父类
        //strategy.setSuperControllerClass("你自己的父类控制器,没有就不用设置!");
        // 写于父类中的公共字段
        strategyConfig.setSuperEntityColumns("id");
        strategyConfig.setInclude(strategy.getString("table").split(","));
        strategyConfig.setControllerMappingHyphenStyle(true);
        strategyConfig.setTablePrefix(pc.getModuleName() + "_");
        mpg.setStrategy(strategyConfig);
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }
}
