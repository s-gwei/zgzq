package org.jeecg.modules.system.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.system.Incident.Incident;
import org.jeecg.modules.system.Incident.IncidentThing;
import org.jeecg.modules.system.entity.ThingTemplate;
import org.jeecg.modules.system.model.PropertyDefinition;
import org.jeecg.modules.system.service.IThingTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author xduan
 * @version 2020/3/5
 * 物模型模板的控制层
 */
@RestController
@RequestMapping("/thingTemplate")
@Slf4j
@Api("物模型模板api")
public class ThingTemplateController {

    @Autowired
    private IThingTemplateService thingTemplateService;


    /**
     * 新增接口
     *
     * @param thingTemplate
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation("新增物模型")
    public Result<ThingTemplate> add(@RequestBody ThingTemplate thingTemplate) {
        Result<ThingTemplate> result = new Result<ThingTemplate>();
        try {
            String name = thingTemplate.getName();
            //判断名称是否重复
            ThingTemplate thingTemplate2 = thingTemplateService.findByName(name);
            if (thingTemplate2 != null) {
                return result.error500("模板名不能重复");
            }
            //新增物模型模板
            boolean save = thingTemplateService.save(thingTemplate);
            if (save) {
                result.setResult(thingTemplate);
                result.success("添加成功！");
            } else {
                result.error500("操作失败");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500(e.getMessage());
        }
        return result;
    }

    /**
     * 根据类型查询
     * 2000 查询系统内置物模型模板名称
     * 2001 查询新建的物模型模板名称
     *
     * @param type
     * @return
     */
    @RequestMapping(value = "/findTemplateByType", method = RequestMethod.GET)
    @ApiOperation("根据类型查询模板")
    public List<String> findTemplateByType(@RequestParam(name = "type", required = true) Integer type) {
        return thingTemplateService.findByType(type);
    }

    /**
     * 根据名称查询模板
     *
     * @param name
     * @return
     */
    @RequestMapping(value = "/findTemplateByName", method = RequestMethod.GET)
    @ApiOperation("根据名称查询模板")
    public Result<?> findTemplateByName(@RequestParam(name = "name", required = true) String name) {
        ThingTemplate thingTemplate = thingTemplateService.findByName(name);
        return Result.ok(thingTemplate);
    }

    @RequestMapping(value = "/deleteByName", method = RequestMethod.DELETE)
    @ApiOperation("根据名称删除模板")
    public Result<?> deleteByName(@RequestParam(name = "name", required = true) String name) {
        Boolean deleteFlag = null;
        try {
            deleteFlag = thingTemplateService.deleteByName(name);
        } catch (JeecgBootException e) {
            log.error(e.toString());
            return Result.error(e.getMessage());
        }
        if (!deleteFlag) {
            //删除失败
            return Result.error(9000, "设备模板有继承设备，不可删除。");
        }
        return Result.ok(name + ",删除成功");
    }

    /**
     * 批量删除
     *
     * @param nameList
     * @return
     */
    @RequestMapping(value = "/deleteByNameList", method = RequestMethod.DELETE)
    @ApiOperation("根据名称批量删除模板")
    public Result<?> deleteByNameList(String nameList) {
        if (StringUtils.isEmpty(nameList)) {
            return Result.error("nameList为空");
        }
        String[] split = nameList.split(",");
        for (String name : split) {
            Boolean deleteFlag = null;
            try {
                deleteFlag = thingTemplateService.deleteByName(name);
            } catch (JeecgBootException e) {
                log.error(e.toString());
                return Result.error(e.getMessage());
            }
            if (!deleteFlag) {
                //删除失败
                return Result.error(9000, name + ",设备模板有继承设备，不可删除。");
            }
        }
        return Result.ok(JSON.toJSONString(nameList) + ",删除成功");
    }

    /**
     * <p>修改模板基础信息，根据name</p>
     * <p>不能修改name 和 所继承的模板 </p>
     * <p>不包括扩展属性的修改</p>
     *
     * @param thingTemplate
     * @return
     */
    @RequestMapping(value = "/updateByName", method = RequestMethod.PUT)
    @ApiOperation("根据名称修改模板")
    public Result<?> updateByName(@RequestBody ThingTemplate thingTemplate, HttpServletRequest request) {
        boolean b = thingTemplateService.updateByName(thingTemplate);
        if (b) {
            return Result.ok("修改成功");
        } else {
            return Result.error("修改失败");
        }
    }

    /**
     * 根据模板名称，给模板添加属性
     *
     * @param propertyDefinition
     * @param templateName
     * @return
     */
    @RequestMapping(value = "/addProperty", method = RequestMethod.POST)
    @ApiOperation("给模板添加自定义属性")
    public Result<?> addProperty(@RequestBody PropertyDefinition propertyDefinition, String templateName) {
        Result<?> result = new Result<>();
        try {
            thingTemplateService.addProperty(propertyDefinition, templateName);
            result.success("添加成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500(e.getMessage());
        }
        return result;
    }

    /**
     * 根据模板名称和属性名称删除可扩展属性
     *
     * @param propertyDefinition
     * @param templateName
     * @return
     */
    @RequestMapping(value = "/updateProperty", method = RequestMethod.PUT)
    @ApiOperation("修改属性")
    @IncidentThing()//自定义注解
    public Result<?> updateProperty(@RequestBody PropertyDefinition propertyDefinition, String templateName) {
        thingTemplateService.updateProperty(propertyDefinition, templateName);
        return Result.ok("修改成功");
    }

    /**
     * 根据模板名称， 修改可扩展属性
     *
     * @param propertyName
     * @param templateName
     * @return
     */
    @RequestMapping(value = "/deleteProperty", method = RequestMethod.DELETE)
    @ApiOperation("删除属性")
    public Result<?> deleteProperty(String propertyName, String templateName) {
        thingTemplateService.deleteProperty(propertyName, templateName);
        return Result.ok("删除成功");
    }

    /**
     * 根据模板名称， 修改可扩展属性
     * 批量删除
     *
     * @param propertyNameList
     * @param templateName
     * @return
     */
    @RequestMapping(value = "/deletePropertyList", method = RequestMethod.DELETE)
    @ApiOperation("删除属性")
    public Result<?> deletePropertyList(String propertyNameList, String templateName) {
        thingTemplateService.deletePropertyList(propertyNameList, templateName);
        return Result.ok("删除成功");
    }

    /**
     * 查询物模型模板列表，（当前用户能查询的所有物模型模板）
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiOperation("查询模板列表,权限控制")
    public Result<List<ThingTemplate>> list(String userId) {
        Result<List<ThingTemplate>> result = new Result<List<ThingTemplate>>();
        List<ThingTemplate> thingTemplateList = thingTemplateService.findByUserId(userId);
        result.setResult(thingTemplateList);
        result.success("操作成功!");
        return result;
    }

    /**
     * 根据条件模糊查询
     *
     * @param templateName
     * @param description
     * @param tags
     * @param projectName
     * @param userId       当前用户id
     * @return
     */
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    @ApiOperation("根据条件模糊查询模板列表")
    public Result<IPage<ThingTemplate>> query(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                              String templateName, String description, String tags, String projectName, String userId) {
        Result<IPage<ThingTemplate>> result = new Result<IPage<ThingTemplate>>();
        try {
            Page page = new Page(pageNo, pageSize);
            IPage<ThingTemplate> templateIPage = thingTemplateService.query(page, templateName, description, tags, projectName, userId);
            result.setResult(templateIPage);
            result.setSuccess(true);
            result.setCode(CommonConstant.SC_OK_200);
        } catch (Exception e) {
            log.error(e.toString());
            result.error500(e.getMessage());
        }
        return result;
    }

    /**
     * 根据设备（物模型实例） 查询物模型模板
     *
     * @param thingName
     * @param userId
     * @return
     */
    @RequestMapping(value = "/queryByThingName", method = RequestMethod.GET)
    @ApiOperation("根据设备查询模板")
    public Result<ThingTemplate> queryByThingName(String thingName, String userId) {
        Result<ThingTemplate> result = new Result<ThingTemplate>();
        try {
            ThingTemplate thingTemplate = thingTemplateService.queryByThingName(thingName, userId);
            result.setResult(thingTemplate);
            result.setSuccess(true);
            result.setCode(CommonConstant.SC_OK_200);
        } catch (Exception e) {
            log.error(e.toString());
            result.error500(e.getMessage());
        }
        return result;
    }

    /**
     * 根据组织/部门查询模板
     *
     * @param departName
     * @return
     */
    @RequestMapping(value = "/queryByDepart", method = RequestMethod.GET)
    @ApiOperation("根据组织查询模板")
    public Result<List<ThingTemplate>> queryByDepart(String departName) {
        Result<List<ThingTemplate>> result = new Result<List<ThingTemplate>>();
        try {
            List<ThingTemplate> thingTemplateList = thingTemplateService.queryByDepart(departName);
            result.setResult(thingTemplateList);
            result.setSuccess(true);
            result.setCode(CommonConstant.SC_OK_200);
        } catch (Exception e) {
            log.error(e.toString());
            result.error500(e.getMessage());
        }
        return result;
    }

    /**
     * 查询所有模板名称，创建设备时使用
     *
     * @return
     */
    @RequestMapping(value = "/getThingTemplateNameList", method = RequestMethod.GET)
    @ApiOperation("查询所有模板名称，没有权限控制")
    public Result<List<String>> getThingTemplateNameList() {
        Result<List<String>> result = new Result<List<String>>();
        try {
            List<ThingTemplate> list = thingTemplateService.list();
            List<String> nameList = new ArrayList<String>(list.size());
            for (ThingTemplate thingTemplate : list) {
                nameList.add(thingTemplate.getName());
            }
            result.setResult(nameList);
            result.setSuccess(true);
            result.setCode(CommonConstant.SC_OK_200);
        } catch (Exception e) {
            log.error(e.toString());
            result.error500(e.getMessage());
        }
        return result;
    }

    /**
     * 查询所有模板的标签列表
     *
     * @return
     */
    @RequestMapping(value = "/getThingTemplateTagsList", method = RequestMethod.GET)
    @ApiOperation("查询所有模板的标签列表，没有权限控制")
    public Result<List<String>> getThingTemplateTagsList() {
        Result<List<String>> result = new Result<List<String>>();
        try {
            List<ThingTemplate> list = thingTemplateService.list();
            List<String> tagList = new ArrayList<String>(list.size());
            for (ThingTemplate thingTemplate : list) {
                String tags = thingTemplate.getTags();
                if (StringUtils.hasText(tags)) {
                    //如果标签不是空,则将标签处理成数组，添加到结果中
                    String[] tagsArray = tags.split(",");
                    Collections.addAll(tagList, tagsArray);
                }
            }
            result.setResult(tagList);
            result.setSuccess(true);
            result.setCode(CommonConstant.SC_OK_200);
        } catch (Exception e) {
            log.error(e.toString());
            result.error500(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/property/list", method = RequestMethod.GET)
    @ApiOperation("根据模板名称 查询模板所有的属性（完整数据）")
    public Result<List<PropertyDefinition>> getPropertyList(String thingTemplateName) {
        Result<List<PropertyDefinition>> result = new Result<List<PropertyDefinition>>();
        try {
            if (StringUtils.isEmpty(thingTemplateName)) {
                throw new Exception("参数'thingTemplateName': 不能为空");
            }
            List<PropertyDefinition> propertyDefinitions = thingTemplateService.getPropertyList(thingTemplateName);
            result.setResult(propertyDefinitions);
            result.setSuccess(true);
            result.setCode(CommonConstant.SC_OK_200);
        } catch (Exception e) {
            log.error(e.toString());
            result.error500(e.getMessage());
        }
        return result;
    }

    /**
     * flag true 向上查父集， false 向下查子集
     * @param templateName
     * @param flag
     * @return
     */
    @GetMapping("/getTree")
    @ApiOperation("获取模板的继承树")
    public Result<JSONObject> getTree(String templateName, Boolean flag) {
        Result<JSONObject> result = new Result<JSONObject>();

        JSONObject tree = thingTemplateService.getTree(templateName, flag);
        result.setResult(tree);
        result.setSuccess(true);
        result.setCode(CommonConstant.SC_OK_200);
        return result;
    }

}
