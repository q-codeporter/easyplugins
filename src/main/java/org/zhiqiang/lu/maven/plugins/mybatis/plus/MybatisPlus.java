package org.zhiqiang.lu.maven.plugins.mybatis.plus;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

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
      Object obj = yamlReader.readValue(
          yaml.dump(yaml.load(new BufferedInputStream(new FileInputStream(projectPath + "/" + configurationFile)))),
          Object.class);
      ObjectMapper jsonWriter = new ObjectMapper();
      properties = JSON.parseObject(jsonWriter.writeValueAsString(obj));
    } catch (IOException e) {
      System.out.println("加载配置问件异常");
      e.printStackTrace();
    }

    JSONObject easyplugins = properties.getJSONObject("easyplugins");
    JSONObject mybatis_plus = easyplugins.getJSONObject("mybatis-plus");
    JSONObject global = mybatis_plus.getJSONObject("global");
    JSONObject datasource = mybatis_plus.getJSONObject("datasource");
    JSONObject application = mybatis_plus.getJSONObject("package");
    JSONObject strategy = mybatis_plus.getJSONObject("strategy");
    JSONObject fileOverride = global.getJSONObject("file-override");

    // 配置策略
    // 1、全局配置
    GlobalConfig gc = new GlobalConfig();
    gc.setOutputDir(projectPath + "/src/main/java");// 生成文件输出根目录
    gc.setAuthor("Q");// 作者
    gc.setOpen(false); // 生成完成后不弹出文件框
    gc.setFileOverride(false); // 文件是否覆盖
    gc.setIdType(IdType.AUTO); // 主键策略 实体类主键ID类型
    gc.setDateType(DateType.ONLY_DATE);
    gc.setSwagger2(global.getBoolean("swagger")); // 是否开启swagger
    gc.setActiveRecord(true); // 【不懂】 活动记录 不需要ActiveRecord特性的请改为false 是否支持AR模式
    gc.setEnableCache(false);// XML 二级缓存
    gc.setBaseResultMap(true);// 【不懂】 XML ResultMap xml映射文件的配置
    gc.setBaseColumnList(false);// 【不懂】 XML columList xml映射文件的配置

    // 自定义文件命名，注意 %s 会自动填充表实体属性！
    gc.setControllerName("%sController");
    gc.setServiceName("%sService");
    gc.setServiceImplName("%sServiceImpl");
    gc.setMapperName("%sMapper");
    gc.setXmlName("%sMapper");
    mpg.setGlobalConfig(gc);

    // 2、设置数据源
    DataSourceConfig dataSourceConfig = new DataSourceConfig();
    dataSourceConfig.setDbType(DbType.MYSQL);
    dataSourceConfig.setUrl(datasource.getString("url"));
    dataSourceConfig.setDriverName(datasource.getString("driver-class-name"));// JDK8
    dataSourceConfig.setUsername(datasource.getString("username"));
    dataSourceConfig.setPassword(datasource.getString("password"));
    // dataSourceConfig.setSchemaName("public");
    mpg.setDataSource(dataSourceConfig);

    // 包配置
    PackageConfig pc = new PackageConfig();
    pc.setModuleName(application.getString("name"));
    pc.setParent(application.getString("parent"));
    pc.setController("controller"); // 可以不用设置，默认是这个
    pc.setService("service"); // 同上
    pc.setServiceImpl("service.impl"); // 同上
    pc.setMapper("dao"); // 默认是mapper
    pc.setEntity("entity"); // 默认是entity
    pc.setXml("mapping"); // 默认是默认是mapper.xml
    pc.setModuleName(null); // 控制层请求地址的包名显示
    mpg.setPackageInfo(pc);

    // 4、策略配置
    StrategyConfig strategyConfig = new StrategyConfig();
    strategyConfig.setInclude(strategy.getString("table").split(",")); // 需要生成的表 设置要映射的表名
    strategyConfig.setNaming(NamingStrategy.underline_to_camel);// 表名生成策略
    strategyConfig.setColumnNaming(NamingStrategy.underline_to_camel);
    strategyConfig.setEntityLombokModel(true); // 自动lombok；
    strategyConfig.setCapitalMode(false); // 【不懂】 开启全局大写命名
    strategyConfig.setSuperMapperClass(null); // 【不懂】
    // 是否需要开启特定规范字段
    if (true == strategy.getBoolean("is-normalize")) {
      strategyConfig.setLogicDeleteFieldName("deleted");
      // 自动填充配置
      TableFill gmtCreate = new TableFill("gmt_create", FieldFill.INSERT);
      TableFill gmtModified = new TableFill("gmt_modified", FieldFill.INSERT_UPDATE);
      ArrayList<TableFill> tableFills = new ArrayList<>();
      tableFills.add(gmtCreate);
      tableFills.add(gmtModified);
      strategyConfig.setTableFillList(tableFills);
      // 乐观锁
      strategyConfig.setVersionFieldName("version");
    }
    strategyConfig.setEntityLombokModel(strategy.getBoolean("lombok"));
    strategyConfig.setRestControllerStyle(true); // 控制：true——生成@RsetController false——生成@Controller
    strategyConfig.setControllerMappingHyphenStyle(true); // 【不知道是啥】
    strategyConfig.setEntityTableFieldAnnotationEnable(true); // 表字段注释启动 启动模板中的这个 <#if table.convert>
    strategyConfig.setEntityBooleanColumnRemoveIsPrefix(true); // 是否删除实体类字段的前缀
    strategyConfig.setTablePrefix("mdm_"); // 去掉表名mdm_inf_rec_data中的 mdm_ 类名为InfRecData
    strategyConfig.setControllerMappingHyphenStyle(false); // 控制层mapping的映射地址 false：infRecData true：inf_rec_data
    String[] baseDir = { "entity", "mapper", "service", "service.impl", "controller" };
    for (String tmp : baseDir) {
      Path s = Paths.get(gc.getOutputDir(), String.join("/", pc.getParent().split("\\.")), tmp);
      if (fileOverride.getBoolean(tmp)) {
        try {
          Files.delete(s);
        } catch (IOException e) {
          System.out.println("删除文件失败！");
          e.printStackTrace();
        }
      }
    }
    // 模板生成器
    mpg.setTemplateEngine(new FreemarkerTemplateEngine());
    TemplateConfig tc = new TemplateConfig();
    tc.setController("/mybatis/plus/freemarker/controller.java");
    tc.setService("/mybatis/plus/freemarker/service.java");
    tc.setServiceImpl("/mybatis/plus/freemarker/serviceImpl.java");
    tc.setEntity("/mybatis/plus/freemarker/entity.java");
    tc.setMapper("/mybatis/plus/freemarker/mapper.java");
    tc.setXml("/mybatis/plus/freemarker/mapper.xml");
    mpg.setTemplate(tc).execute(); // 执行
  }
}
