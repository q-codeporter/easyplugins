package org.zhiqiang.lu.maven.plugins.mybatis.generator;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.VerboseProgressCallback;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Mojo(name = "mybatis-generator", defaultPhase = LifecyclePhase.PACKAGE)
public class MybatisGenerator extends AbstractMojo {
  @Parameter
  private String configurationFile;

  public static void main(String[] args) throws MojoExecutionException {
    MybatisGenerator m = new MybatisGenerator();
    m.configurationFile = "/src/main/resources/mybatis/generator/config.xml";
    m.execute();
  }

  public void execute() throws MojoExecutionException {
    // 项目路径
    String projectPath = System.getProperty("user.dir");
    List<String> warnings = new ArrayList<>();
    ConfigurationParser cp = new ConfigurationParser(warnings);
    Configuration config = null;
    try {
      System.out.println(projectPath + "/" + configurationFile);
      config = cp
          .parseConfiguration(new BufferedInputStream(new FileInputStream(projectPath + "/" + configurationFile)));
    } catch (IOException e) {
      System.err.println("easycode-error:读取配置文件异常");
      e.printStackTrace();
    } catch (XMLParserException e) {
      System.err.println("easycode-error:解析配置文件异常");
      e.printStackTrace();
    }

    DefaultShellCallback callback = new DefaultShellCallback(true);
    MyBatisGenerator myBatisGenerator = null;
    try {
      myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
    } catch (InvalidConfigurationException e) {
      System.err.println("easycode-error:创建MyBatisGenerator异常");
      e.printStackTrace();
    }
    try {
      VerboseProgressCallback progressCallback = new VerboseProgressCallback();
      myBatisGenerator.generate(progressCallback);
    } catch (Exception e) {
      System.err.println("easycode-mybatis-generator:生成文件异常");
      e.printStackTrace();
    }
    for (String warning : warnings) {
      System.out.println(warning);
    }
  }
}
