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
import org.zhiqiang.lu.maven.plugins.mybatis.utils.Files;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

@Mojo(name = "mybatis-plus", defaultPhase = LifecyclePhase.PACKAGE)
public class MybatisPlus extends AbstractMojo {
  @Parameter
  private String configurationFile;

  public static void main(String[] args) throws MojoExecutionException {
    MybatisPlus m = new MybatisPlus();
    m.configurationFile = "/src/main/resources/mybatis/plus/mybatis.yml";
    m.execute();
  }

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

    String[] baseDir = { "entity", "mapper", "service", "service.impl", "controller" };
    String module_name = application.getString("module_name") == null ? "" : application.getString("module_name");
    String packagePath = String.join("/", (module_name + "." + application.getString("parent")).split("\\."));
    for (String tmp : baseDir) {
      String path = projectPath + "/src/main/java/" + packagePath + "/" + tmp;
      if (global.getJSONObject("file-override").getBoolean(tmp)) {
        try {
          System.out.println(path);
          Files.delete(path);
        } catch (Exception e) {
          System.out.println("删除文件失败！");
          e.printStackTrace();
        }
      }
    }

    // 配置策略
    // 1、全局配置
    GlobalConfig gc = new GlobalConfig();
    gc.setOutputDir(projectPath + "/src/main/java");// 生成文件输出根目录
    gc.setAuthor("Q");// 作者
    gc.setOpen(false); // 生成完成后不弹出文件框
    gc.setFileOverride(false); // 文件是否覆盖
    gc.setIdType(IdType.ASSIGN_ID); // 主键策略 实体类主键ID类型
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
    mpg.setDataSource(dataSourceConfig);

    // 3、包配置
    PackageConfig pc = new PackageConfig();
    pc.setModuleName(application.getString("module_name")); // 控制层请求地址的包名显示
    pc.setParent(application.getString("parent"));
    pc.setController("controller"); // 可以不用设置，默认是这个
    pc.setService("service"); // 同上
    pc.setServiceImpl("service.impl"); // 同上
    pc.setMapper("mapper"); // 默认是mapper
    pc.setXml("mapper.xml"); // 默认是默认是mapper.xml
    pc.setEntity("entity"); // 默认是entity
    mpg.setPackageInfo(pc);

    // 4、策略配置
    StrategyConfig strategyConfig = new StrategyConfig();
    strategyConfig.setInclude(strategy.getString("table").split(",")); // 需要生成的表 设置要映射的表名
    strategyConfig.setNaming(NamingStrategy.underline_to_camel);// 表名生成策略
    strategyConfig.setColumnNaming(NamingStrategy.underline_to_camel);
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
    strategyConfig.setSuperControllerClass(strategy.getJSONObject("super").getString("controller"));
    strategyConfig.setSuperMapperClass("com.baomidou.mybatisplus.core.mapper.BaseMapper");
    mpg.setStrategy(strategyConfig);

    // 5、模板生成器
    mpg.setTemplateEngine(new FreemarkerTemplateEngine());
    TemplateConfig tc = new TemplateConfig();
    if (strategy.getJSONObject("super").getString("controller").contains("zhiqiang.lu")) {
      tc.setController("/mybatis/plus/freemarker/controller.java");
    }
    // tc.setEntity(null).setXml(null).setMapper(null).setService(null).setServiceImpl(null);
    mpg.setTemplate(tc).execute(); // 执行
  }
}
