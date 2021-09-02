## maven插件 easycode
### mybatis-generator
#### 功能描述：
mybatis-generator：对mybatis-generator进行扩展，添加生成swagger、service、controller、日期与时间格式化，等代码一键生成。由于不建议使用lombok，且类文件都是自动生成不会影响编码效率，所以不支持lombok扩展。
<br/>
#### 使用方法：
maven插件方式引入，使用原生mybatis-generator配置文件<br/>
<!-- mybatis generator 自动生成代码插件 --><br/>
```
<plugin>
    <groupId>org.q</groupId>
    <artifactId>easycode</artifactId>
    <version>2.4.6</version>
    <configuration>
        <configurationFile>src/main/resources/mybaits/generator/config.xml</configurationFile>
    </configuration>
</plugin><br/>
```
mybatis-generator-plugin插件扩展
```
<!-- 自动为entity生成swagger2文档-->
<plugin type="GeneratorSwagger2Doc"/>
<!-- 日期与时间格式化 -->
<plugin type="GeneratorDateTimeFormat">
    <property name="dateFormat" value="yyyy-MM-dd"/>
    <property name="timeFormat" value="yyyy-MM-dd HH:mm:ss"/>
</plugin>
<!-- 扩展entity的set方法-->
<plugin type="ExtendEntitySetter"/>
<!-- 扩展添加services接口-->
<plugin type="GeneratorServices"/>
<!-- 扩展添加controller，开启功能，但并不会直接生成，需要在对应生成的table里开启controller，建议需要用一个controller时生成一个，避免无用接口暴露-->
<plugin type="GeneratorController"/>
<property name="controller" value="true"/>
```
使用示例：
```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration
        PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
        "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">
<!-- 配置生成器 -->
<generatorConfiguration>
    <context id="mysql" targetRuntime="MyBatis3" defaultModelType="flat">
        <property name="autoDelimitKeywords" value="false"/>
        <!-- 生成的Java文件的编码 -->
        <property name="javaFileEncoding" value="UTF-8"/>
        <!-- 格式化java代码 -->
        <property name="javaFormatter" value="org.mybatis.generator.api.dom.DefaultJavaFormatter"/>
        <!-- 格式化XML代码 -->
        <property name="xmlFormatter" value="org.mybatis.generator.api.dom.DefaultXmlFormatter"/>
        <!-- beginningDelimiter和endingDelimiter：指明数据库的用于标记数据库对象名的符号，比如ORACLE就是双引号，MYSQL默认是`反引号； -->
        <property name="beginningDelimiter" value="`"/>
        <property name="endingDelimiter" value="`"/>

        <!--序列化-->
        <plugin type="org.mybatis.generator.plugins.SerializablePlugin"/>
        <!--覆盖生成XML文件-->
        <plugin type="org.mybatis.generator.plugins.UnmergeableXmlMappersPlugin"/>
        <!-- 自动为entity生成swagger2文档-->
        <plugin type="GeneratorSwagger2Doc"/>
        <!-- 时间处理 -->
        <plugin type="GeneratorDateTimeFormat">
            <property name="dateFormat" value="yyyy-MM-dd"/>
            <property name="timeFormat" value="yyyy-MM-dd HH:mm:ss"/>
        </plugin>
        <!-- 扩展entity的set方法-->
        <plugin type="ExtendEntitySetter"/>
        <!-- 扩展添加services接口-->
        <plugin type="GeneratorServices"/>
        <!-- 扩展添加controller-->
        <plugin type="GeneratorController"/>

        <commentGenerator>
            <!--是否生成注释带时间戳-->
            <property name="suppressDate" value="true"/>
            <!-- 是否取消注释 -->
            <property name="suppressAllComments" value="true"/>
        </commentGenerator>
        <!--数据库链接URL，用户名、密码 -->
        <jdbcConnection driverClass="com.mysql.cj.jdbc.Driver"
                        connectionURL="jdbc:mysql://1.62.154.22:6365/cloud_management" userId="root" password="root">
            <property name="useInformationSchema" value="true"/>
        </jdbcConnection>
        <javaTypeResolver type="org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl">
            <property name="forceBigDecimals" value="false"/>
        </javaTypeResolver>
        <!-- 生成模型的包名和位置-->
        <javaModelGenerator targetPackage="com.hitsoft.management.entity" targetProject="src/main/java">
            <property name="enableSubPackages" value="true"/>
            <property name="trimStrings" value="true"/>
        </javaModelGenerator>
        <!-- 生成映射文件的包名和位置-->
        <sqlMapGenerator targetPackage="resources.mybaits.mapper" targetProject="src/main">
            <property name="enableSubPackages" value="true"/>
        </sqlMapGenerator>
        <!-- 生成DAO的包名和位置-->
        <javaClientGenerator type="XMLMAPPER" targetPackage="com.hitsoft.management.mapper"
                             targetProject="src/main/java">
            <property name="enableSubPackages" value="true"/>
        </javaClientGenerator>
        <!-- 要生成的表 tableName是数据库中的表名或视图名 domainObjectName是实体类名-->
        <table tableName="authorization">
            <property name="controller" value="true"/>
            <property name="useActualColumnNames" value="true"/>
        </table>
        <table tableName="authorization_order">
            <property name="useActualColumnNames" value="true"/>
        </table>
        <table tableName="authorization_monthly_money">
            <property name="useActualColumnNames" value="true"/>
        </table>
        <table tableName="profile">
            <property name="useActualColumnNames" value="true"/>
        </table>
    </context>
</generatorConfiguration>
```
### mybatis-generator
#### 功能描述：
mybatis-plus：对mybatis-plus进行扩展。
<br/>
#### 使用方法：
```
easyplugins:
  mybatis-plus:
    package:
      module_name: 
      parent: com.example.test
    global:
      swagger: true
      file-override:
        entity: true
        mapper: true
        service: false
        service.impl: false
        controller: false
    datasource:
      url: jdbc:mysql://1.62.154.22:6365/dataway?useSSL=false&useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8
      username: root
      password: root
      driver-class-name: com.mysql.jdbc.Driver
    template:
      type: freemarker
    strategy:
      super:
        controller: org.zhiqiang.lu.easycode.spring.aop.model.mybatis.plus.swagger.BaseController
      is-normalize: false
      lombok: false
      table: interface_info,interface_release,users
```



