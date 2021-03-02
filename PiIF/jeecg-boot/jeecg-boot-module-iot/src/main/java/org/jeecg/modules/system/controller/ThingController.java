package org.jeecg.modules.system.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.system.Incident.Incident;
import org.jeecg.modules.system.entity.Thing;
import org.jeecg.modules.system.model.NodeTreeModel;
import org.jeecg.modules.system.model.PropertyDefinition;
import org.jeecg.modules.system.service.IThingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author xduan
 * @version 2020/3/10
 * 物模型实例 控制层
 */
@RestController
@RequestMapping("/thing")
@Slf4j
@Api("物模型实例api")
public class ThingController {

    @Autowired
    private IThingService thingService;

    /**
     * 新增接口
     *
     * @param thing
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation("新增物模型实例/设备")
    public Result<Thing> add(@RequestBody Thing thing, String username) {
        Result<Thing> result = new Result<Thing>();
        try {
            String name = thing.getName();
            //判断名称是否重复
            Thing byId = thingService.getById(name);
            if (byId != null) {
                return result.error500("设备名不能重复");
            }
            //新增物模型模板
            thing = thingService.add(thing, username);
            result.setResult(thing);
            result.setSuccess(true);
            result.setCode(CommonConstant.SC_OK_200);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500(e.getMessage());
        }
        return result;
    }

    /**
     * 根据name查询设备
     *
     * @param thingName
     * @return
     */
    @RequestMapping(value = "/findByName", method = RequestMethod.GET)
    @ApiOperation("根据设备名称查询,没有权限控制")
    public Result<Thing> findByName(String thingName) {
        Result<Thing> result = new Result<Thing>();
        try {
            Thing thing = thingService.getById(thingName);
            result.setResult(thing);
            result.setSuccess(true);
            result.setCode(CommonConstant.SC_OK_200);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500(e.getMessage());
        }
        return result;
    }

    /**
     * 修改设备，不能修改name和继承的模板
     *
     * @param username
     * @param thing
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @ApiOperation("修改设备")
    public Result<Thing> update(String username, @RequestBody Thing thing) {
        Result<Thing> result = new Result<Thing>();
        try {
            Thing thingNew = thingService.update(username, thing);
            result.setResult(thingNew);
            result.setSuccess(true);
            result.setCode(CommonConstant.SC_OK_200);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/deleteByName", method = RequestMethod.DELETE)
    @ApiOperation("根据名称删除设备")
    public Result<?> deleteByName(String name) {
        try {
            thingService.deleteByName(name);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
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
    @ApiOperation("根据名称批量删除设备")
    public Result<?> deleteByNameList(String nameList) {
        if (StringUtils.isEmpty(nameList)) {
            return Result.error("nameList为空");
        }
        //将名称字符串处理成数组
        String[] split = nameList.split(",");
        for (String name : split) {
            try {
                thingService.deleteByName(name);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error(e.getMessage());
            }
        }
        return Result.ok(JSON.toJSONString(nameList) + ",删除成功");
    }


    /**
     * 根据模板名称，给模板添加属性
     *
     * @param propertyDefinition
     * @param thingName
     * @return
     */
    @RequestMapping(value = "/addProperty", method = RequestMethod.POST)
    @ApiOperation("给设备添加自定义属性")
    public Result<?> addProperty(@RequestBody PropertyDefinition propertyDefinition, String thingName, String username) {
        Result<?> result = new Result<>();
        thingService.addProperty(propertyDefinition, thingName, username);
        result.success("添加成功");
        return result;
    }

    /**
     * 根据模板名称和属性名称删除可扩展属性
     *
     * @param propertyDefinition
     * @param thingName
     * @return
     */
    @RequestMapping(value = "/updateProperty", method = RequestMethod.PUT)
    @ApiOperation("修改属性")
    @Incident()//自定义注解
    public Result<?> updateProperty(@RequestBody PropertyDefinition propertyDefinition, String thingName, String username) {
        thingService.updateProperty(propertyDefinition, thingName, username);
        return Result.ok("修改成功");
    }

    /**
     * 根据模板名称， 修改可扩展属性
     *
     * @param propertyName
     * @param thingName
     * @return
     */
    @RequestMapping(value = "/deleteProperty", method = RequestMethod.DELETE)
    @ApiOperation("删除属性")
    public Result<?> deleteProperty(String propertyName, String thingName, String username) {
        thingService.deleteProperty(propertyName, thingName, username);
        return Result.ok("删除成功");
    }


    /**
     * 查询物模型实例列表，（当前用户能查询的所有物模型模板）
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiOperation("查询设备列表,权限控制")
    public Result<List<Thing>> list(String userId) {
        Result<List<Thing>> result = new Result<List<Thing>>();
        List<Thing> thingTemplateList = thingService.findByUserId(userId);
        result.setResult(thingTemplateList);
        result.setSuccess(true);
        result.setCode(CommonConstant.SC_OK_200);
        return result;
    }

    /**
     * 根据条件模糊查询
     *
     * @param thingName
     * @param description
     * @param tags
     * @param projectName
     * @param userId      当前用户id
     * @return
     */
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    @ApiOperation("根据条件模糊查询模板列表")
    public Result<IPage<Thing>> query(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                      String thingName, String description, String tags, String projectName, String userId) {
        Result<IPage<Thing>> result = new Result<IPage<Thing>>();
        try {
            Page page = new Page(pageNo, pageSize);
            IPage<Thing> pageThing = thingService.query(page, thingName, description, tags, projectName, userId);
            result.setResult(pageThing);
            result.setSuccess(true);
            result.setCode(CommonConstant.SC_OK_200);
        } catch (Exception e) {
            log.error(e.toString());
            result.error500(e.getMessage());
        }
        return result;
    }

    /**
     * 根据物模型模板 查询设备（物模型实例）
     *
     * @param templateName
     * @param userId
     * @return
     */
    @RequestMapping(value = "/queryByTemplateName", method = RequestMethod.GET)
    @ApiOperation("根据物模型模板 查询设备（物模型实例）")
    public Result<List<Thing>> queryByTemplateName(String templateName, String userId) {
        Result<List<Thing>> result = new Result<List<Thing>>();
        try {
            List<Thing> thingList = thingService.queryByTemplateName(templateName, userId);
            result.setResult(thingList);
            result.setSuccess(true);
            result.setCode(CommonConstant.SC_OK_200);
        } catch (Exception e) {
            log.error(e.toString());
            result.error500(e.getMessage());
        }
        return result;
    }

    /**
     * 根据属性名，查询设备
     *
     * @param propertyName
     * @param userId
     * @return
     */
    @RequestMapping(value = "/queryByPropertyName", method = RequestMethod.GET)
    @ApiOperation("根据属性名称 查询设备（物模型实例）")
    public Result<List<Thing>> queryByPropertyName(String propertyName, String userId) {
        Result<List<Thing>> result = new Result<List<Thing>>();
        try {
            List<Thing> thingList = thingService.queryByPropertyName(propertyName, userId);
            result.setResult(thingList);
            result.setSuccess(true);
            result.setCode(CommonConstant.SC_OK_200);
        } catch (Exception e) {
            log.error(e.toString());
            result.error500(e.getMessage());
        }
        return result;
    }


    /**
     * 根据设备名称查询设备所有属性
     *
     * @param thingName
     * @return 将继承属性和设备属性分开返回
     */
    @RequestMapping(value = "/property/list2", method = RequestMethod.GET)
    @ApiOperation("根据设备名称 查询设备所有的属性（属性分成2个数组）")
    public Result<JSONArray> getPropertyList2(String thingName) {
        Result<JSONArray> result = new Result<>();
        try {
            JSONArray jsonArray = thingService.getPropertyList2(thingName);
            result.setResult(jsonArray);
            result.setSuccess(true);
            result.setCode(CommonConstant.SC_OK_200);
        } catch (Exception e) {
            log.error(e.toString());
            result.error500(e.getMessage());
        }
        return result;
    }

    /**
     * 根据设备名称查询设备所有属性
     *
     * @param thingName
     * @return
     */
    @RequestMapping(value = "/property/list", method = RequestMethod.GET)
    @ApiOperation("根据设备名称 查询设备（物模型实例）所有的属性（完整数据）")
    public Result<List<PropertyDefinition>> getPropertyList(String thingName) {
        Result<List<PropertyDefinition>> result = new Result<List<PropertyDefinition>>();
        try {
            List<PropertyDefinition> propertyDefinitions = thingService.getPropertyList(thingName);
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
     * 根据设备名称查询设备所有属性名称和值
     *
     * @param thingName
     * @return
     */
    @RequestMapping(value = "/property/value/list", method = RequestMethod.GET)
    @ApiOperation("根据设备名称 查询设备（物模型实例）所有的属性名称和值")
    public Result<Map<String, String>> getPropertyValueList(String thingName) {
        Result<Map<String, String>> result = new Result<Map<String, String>>();
        try {
            Map<String, String> propertyValueMap = thingService.getPropertyValueList(thingName);
            result.setResult(propertyValueMap);
            result.setSuccess(true);
            result.setCode(CommonConstant.SC_OK_200);
        } catch (Exception e) {
            log.error(e.toString());
            result.error500(e.getMessage());
        }
        return result;
    }

    /**
     * 根据设备名称和属性名称（多个），返回属性（name+value）
     *
     * @param thingName
     * @param propertyNameList 查询的属性名列表
     * @return
     */
    @RequestMapping(value = "getPropertyValueByPropertyName", method = RequestMethod.POST)
    @ApiOperation("根据设备名称和属性名称（多个），返回属性（name+value）")
    public Result<Map<String, String>> getPropertyValueByPropertyName(String thingName, @RequestBody List<String> propertyNameList) {
        Result<Map<String, String>> result = new Result<Map<String, String>>();
        try {
            Map<String, String> propertyValueMap = thingService.getPropertyValueByPropertyName(thingName, propertyNameList);
            result.setResult(propertyValueMap);
            result.setSuccess(true);
            result.setCode(CommonConstant.SC_OK_200);
        } catch (Exception e) {
            log.error(e.toString());
            result.error500(e.getMessage());
        }
        return result;
    }

    /**
     * 根据设备名称+属性名称（多个）修改对应的值
     *
     * @param thingName         设备名
     * @param username          用户名
     * @param propertyNameValue 修改属性的name 和value的键值对
     * @return
     */
    @RequestMapping(value = "updatePropertyValue", method = RequestMethod.PUT)
    @ApiOperation("根据设备名称+属性名称（多个）修改对应的值")
    public Result<?> updatePropertyValue(String thingName, String username, @RequestBody Map<String, String> propertyNameValue) {
        try {
            thingService.updatePropertyValue(thingName, username, propertyNameValue);
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error(e.getMessage());
        }
        return Result.ok("修改成功");
    }

    /**
     * 根据设备名称+属性名称（多个）删除属性
     *
     * @param thingName
     * @param username
     * @param propertyNameList
     * @return
     */
    @RequestMapping(value = "deletePropertyList", method = RequestMethod.DELETE)
    @ApiOperation("根据设备名称+属性名称（多个）删除属性")
    public Result<?> deletePropertyList(String thingName, String username, String propertyNameList) {
        try {
            if (StringUtils.isEmpty(propertyNameList)) {
                return Result.error("propertyNameList 不能为空");
            }
            String[] split = propertyNameList.split(",");
            List<String> propertyNames = new ArrayList<String>(Arrays.asList(split));
            thingService.deletePropertyList(thingName, username, propertyNames);
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error(e.getMessage());
        }
        return Result.ok("修改成功");
    }

    /* */

    /**
     * 查询所有设备的标签列表
     * 废弃
     *
     * @return
     *//*
    @RequestMapping(value = "/getThingTagsList", method = RequestMethod.GET)
    @ApiOperation("查询所有设备的标签列表，没有权限控制")
    public Result<List<String>> getThingTagsList() {
        Result<List<String>> result = new Result<List<String>>();
        try {
            List<Thing> list = thingService.list();
            List<String> tagList = new ArrayList<String>(list.size());
            for (Thing thing : list) {
                String tags = thing.getTags();
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
    }*/
    @GetMapping("/getTree")
    @ApiOperation("获取设备的继承树")
    public Result<NodeTreeModel> getTree(String thingName) {
        Result<NodeTreeModel> result = new Result<NodeTreeModel>();
        NodeTreeModel tree = thingService.getTree(thingName);
        result.setResult(tree);
        result.setSuccess(true);
        result.setCode(CommonConstant.SC_OK_200);
        return result;
    }

}
