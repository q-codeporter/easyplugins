package org.zhiqiang.lu.maven.plugins.mybatis.plus;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * @description:
 * @author: liyh
 * @time: 2020/12/21 21:20
 */

// 配置文件位置
public class GenerateMybatisPlus {

  /**
   * @param dataSourceurl
   * @param dataSourcename
   * @param dataSourcepassword
   * @param dataSourcedriver
   * @param tables
   * @param packageParent
   * @param isNormalize
   * @description:
   * @return: void
   * @author: liyh
   * @time: 2020/12/21 21:21
   */
  public void generate(String dataSourceurl, String dataSourcename, String dataSourcepassword, String dataSourcedriver,
      String tables, String packageParent, boolean isNormalize) {

    AutoGenerator mpg = new AutoGenerator();
    // 配置策略
    // 1、全局配置
    GlobalConfig gc = new GlobalConfig();
    String projectPath = System.getProperty("user.dir");// 当前项目的路径
    gc.setOutputDir(projectPath + "/src/main/java");// 生成文件输出根目录
    gc.setAuthor("Q");// 作者
    gc.setOpen(false); // 生成完成后不弹出文件框
    gc.setFileOverride(true); // 文件是否覆盖
    gc.setIdType(IdType.AUTO); // 主键策略 实体类主键ID类型
    gc.setDateType(DateType.ONLY_DATE);
    gc.setSwagger2(true); // 是否开启swagger
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
    DataSourceConfig dsc = new DataSourceConfig();
    dsc.setDbType(DbType.MYSQL);
    dsc.setUrl(dataSourceurl);
    dsc.setDriverName(dataSourcedriver);
    dsc.setUsername(dataSourcename);
    dsc.setPassword(dataSourcepassword);
    mpg.setDataSource(dsc);

    // 3、包的配置
    PackageConfig pc = new PackageConfig();
    pc.setParent(packageParent);
    pc.setController("controller"); // 可以不用设置，默认是这个
    pc.setService("service"); // 同上
    pc.setServiceImpl("service.impl"); // 同上
    pc.setMapper("dao"); // 默认是mapper
    pc.setEntity("entity"); // 默认是entity
    pc.setXml("mapping"); // 默认是默认是mapper.xml
    pc.setModuleName(null); // 控制层请求地址的包名显示
    mpg.setPackageInfo(pc);

    // 4、策略配置
    StrategyConfig strategy = new StrategyConfig();
    strategy.setInclude(tables.split(",")); // 需要生成的表 设置要映射的表名
    strategy.setNaming(NamingStrategy.underline_to_camel);// 表名生成策略
    strategy.setColumnNaming(NamingStrategy.underline_to_camel);
    strategy.setEntityLombokModel(true); // 自动lombok；
    strategy.setCapitalMode(false); // 【不懂】 开启全局大写命名
    strategy.setSuperMapperClass(null); // 【不懂】
    // 是否需要开启特定规范字段
    if (true == isNormalize) {
      strategy.setLogicDeleteFieldName("deleted");
      // 自动填充配置
      TableFill gmtCreate = new TableFill("gmt_create", FieldFill.INSERT);
      TableFill gmtModified = new TableFill("gmt_modified", FieldFill.INSERT_UPDATE);
      ArrayList<TableFill> tableFills = new ArrayList<>();
      tableFills.add(gmtCreate);
      tableFills.add(gmtModified);
      strategy.setTableFillList(tableFills);
      // 乐观锁
      strategy.setVersionFieldName("version");
    }
    strategy.setEntityLombokModel(false);
    strategy.setRestControllerStyle(true); // 控制：true——生成@RsetController false——生成@Controller
    strategy.setControllerMappingHyphenStyle(true); // 【不知道是啥】
    strategy.setEntityTableFieldAnnotationEnable(true); // 表字段注释启动 启动模板中的这个 <#if table.convert>
    strategy.setEntityBooleanColumnRemoveIsPrefix(true); // 是否删除实体类字段的前缀
    strategy.setTablePrefix("mdm_"); // 去掉表名mdm_inf_rec_data中的 mdm_ 类名为InfRecData
    strategy.setControllerMappingHyphenStyle(false); // 控制层mapping的映射地址 false：infRecData true：inf_rec_data
    mpg.setStrategy(strategy);

    // 模板生成器
    mpg.setTemplateEngine(new FreemarkerTemplateEngine());
    TemplateConfig tc = initTemplateConfig(packageParent);
    mpg.setTemplate(tc).execute(); // 执行
  }

  /**
   * 根据自己的需要，修改哪些包下面的 要覆盖还是不覆盖
   * 
   * @param packageName
   */
  private static TemplateConfig initTemplateConfig(String packageParent) {
    TemplateConfig tc = new TemplateConfig();
    String[] baseDir = { "entity", "mapper", "service", "service.impl", "controller" };
    for (String tmp : baseDir) {
      initVM(tc);
      String s = Paths
          .get(System.getProperty("user.dir") + "/src/main/java", String.join("/", packageParent.split("\\.")), tmp)
          .toString();
      File file = new File(s);
      String[] list = file.list();
      if (list != null && list.length > 0) {
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        tc.disable(TemplateType.SERVICE);
        tc.disable(TemplateType.CONTROLLER);
      } else {
        System.out.println("------------------------------------------------------------");
      }
    }
    return tc;
  }

  private static void initVM(TemplateConfig tc) {
    tc.setController("/mybatis/plus/freemarker/controller.java");
    tc.setService("/mybatis/plus/freemarker/service.java");
    tc.setServiceImpl("/mybatis/plus/freemarker/serviceImpl.java");
    tc.setEntity("/mybatis/plus/freemarker/entity.java");
    tc.setMapper("/mybatis/plus/freemarker/mapper.java");
    tc.setXml("/mybatis/plus/freemarker/mapper.xml");
  }

  public static void main(String[] args) {
    String dataSourceurl = "jdbc:mysql://1.62.154.22:6365/kubesphere_manager?useSSL=false&useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8";
    new GenerateMybatisPlus().generate(dataSourceurl, "root", "root", "com.mysql.jdbc.Driver", "approval_process",
        "com.hong.generate.generatedemo", false);
  }
}
