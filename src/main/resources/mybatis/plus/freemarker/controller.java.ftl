package ${package.Controller};



import ${package.Service}.${table.serviceName};
import ${package.Entity}.${entity};
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hong.generate.common.PageResult;
import com.hong.generate.common.Result;
import com.hong.generate.common.StatusCode;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
<#if restControllerStyle>
import org.springframework.web.bind.annotation.RestController;
<#else>
import org.springframework.stereotype.Controller;
</#if>
<#if superControllerClassPackage??>
import ${superControllerClassPackage};
</#if>

/**
 * <p>
 * ${table.comment!} 前端控制器
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
<#if restControllerStyle>
@RestController
<#else>
@Controller
</#if>
@RequestMapping("<#if package.ModuleName??>/${package.ModuleName}</#if>/<#if controllerMappingHyphenStyle??>${controllerMappingHyphen}<#else>${table.entityPath}</#if>")
<#if kotlin>
class ${table.controllerName}<#if superControllerClass??> : ${superControllerClass}()</#if>
<#else>
<#if superControllerClass??>
public class ${table.controllerName} extends ${superControllerClass} {
<#else>
public class ${table.controllerName} {

    @Autowired
    public ${table.serviceName} ${table.entityPath}Service;

    @ApiOperation(value = "新增")
    @PostMapping("/save")
    public boolean save(@RequestBody ${entity} ${table.entityPath}){
        return ${table.entityPath}Service.save(${table.entityPath});
    }

    @ApiOperation(value = "根据id删除")
    @PostMapping("/delete/{id}")
    public boolean delete(@PathVariable("id") Long id){
        return ${table.entityPath}Service.removeById(id);
    }

    @ApiOperation(value = "条件查询")
    @PostMapping("/get")
    public List<${entity}> list(@RequestBody ${entity} ${table.entityPath}){
        return ${table.entityPath}Service.list(new QueryWrapper<>(${table.entityPath}));
    }

    @ApiOperation(value = "列表（分页）")
    @GetMapping("/list/{pageNum}/{pageSize}")
    public PageResult list(@PathVariable("pageNum")Long pageNum, @PathVariable("pageSize")Long pageSize){
         IPage<${entity}> page = ${table.entityPath}Service.page(
                new Page<>(pageNum, pageSize),
                null);
         return new PageResult<>(page.getTotal(),page.getRecords())
    }

    @ApiOperation(value = "详情")
    @GetMapping("/get/{id}")
    public ${entity} get(@PathVariable("id") Long id){
        return ${table.entityPath}Service.getById(id);
    }

    @ApiOperation(value = "根据id修改")
    @PostMapping("/update/{id}")
    public boolean update(@PathVariable("id") Long id, @RequestBody ${entity} ${table.entityPath}){
        ${table.entityPath}.setId(id);
        return ${table.entityPath}Service.updateById(${table.entityPath});
    }

</#if>

}
</#if>
