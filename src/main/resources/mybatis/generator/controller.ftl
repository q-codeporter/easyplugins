package ${topPackage}.controller;

import ${javaModelTargetPackage}.${EntityName};
import ${topPackage}.service.${EntityName}Service;
import org.zhiqiang.lu.easycode.spring.aop.model.LogicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

<#list packages as package>
    import ${package};
</#list>

/**
*  generated by Q's plugin
*/


@RestController
@RequestMapping("/${entity_name}")
public class ${EntityName}Controller {

@Autowired
private ${EntityName}Service ${entityName}Service;

@PostMapping("/insert")
public ${EntityName} insert(@RequestBody ${EntityName} ${entityName}) throws LogicException {
try{
${entityName}Service.insert(${entityName});
return ${entityName};
} catch (DuplicateKeyException e) {
throw new LogicException("<#list primary_key as pk>[${pk.remarks}]<#if pk_has_next>,</#if></#list>信息重复！");
}
}

@PostMapping("/update")
public int update(@RequestBody ${EntityName} ${entityName}) {
return ${entityName}Service.updateByPrimaryKeySelective(${entityName});
}

@GetMapping("/delete")
public int delete(<#list primary_key as pk>@RequestParam ${pk.fullyQualifiedJavaType.shortName} ${pk.actualColumnName}<#if pk_has_next>,</#if></#list>) {
return ${entityName}Service.deleteByPrimaryKey(<#list primary_key as pk>${pk.actualColumnName}<#if pk_has_next>,</#if></#list>);
}

@GetMapping("/select")
public ${EntityName} select(<#list primary_key as pk>@RequestParam ${pk.fullyQualifiedJavaType.shortName} ${pk.actualColumnName}<#if pk_has_next>,</#if></#list>) {
return ${entityName}Service.selectByPrimaryKey(<#list primary_key as pk>${pk.actualColumnName}<#if pk_has_next>,</#if></#list>);
}
}