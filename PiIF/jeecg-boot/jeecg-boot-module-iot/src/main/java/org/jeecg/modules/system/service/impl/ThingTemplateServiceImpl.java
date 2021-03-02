package org.jeecg.modules.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.util.TokenUtils;
import org.jeecg.modules.iot.entity.ThingtemplateDepart;
import org.jeecg.modules.iot.mapper.RoleobjectPermissionMapper;
import org.jeecg.modules.iot.mapper.ThingDepartMapper;
import org.jeecg.modules.iot.mapper.ThingTemplateDepartMapper;
import org.jeecg.modules.iot.mapper.UserobjectPermissionMapper;
import org.jeecg.modules.system.entity.Thing;
import org.jeecg.modules.system.entity.ThingTemplate;
import org.jeecg.modules.system.mapper.ThingMapper;
import org.jeecg.modules.system.mapper.ThingTemplateMapper;
import org.jeecg.modules.system.model.PropertyDefinition;
import org.jeecg.modules.system.model.ThingShape;
import org.jeecg.modules.system.service.IPermissionService;
import org.jeecg.modules.system.service.IThingTemplateService;
import org.jeecg.modules.system.util.PropertyUtil;
import org.jeecg.modules.system.util.ThingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xduan
 * @version 2020/3/5
 */
@Slf4j
@Service
public class ThingTemplateServiceImpl extends ServiceImpl<ThingTemplateMapper, ThingTemplate> implements IThingTemplateService {
    private static final String THINGTEMPLATE_MODEL = "iot_thingtemplate_model";

    @Resource
    private ThingTemplateMapper thingTemplateMapper;

    @Resource
    private ThingMapper thingMapper;


    @Resource
    private ThingDepartMapper thingDepartMapper;

    @Resource
    private ThingTemplateDepartMapper thingTemplateDepartMapper;

    @Resource
    private RoleobjectPermissionMapper roleobjectPermissionMapper;

    @Resource
    private UserobjectPermissionMapper userobjectPermissionMapper;

    @Resource
    private IPermissionService permissionService;

    @Autowired
    private ThingUtil thingUtil;

    /**
     * 新增模板
     *
     * @param entity
     * @return 操作成功返回 true ,否则返回false
     */
    @Override
    public boolean save(ThingTemplate entity) {

        String baseThingTemplateStr = entity.getBaseThingTemplate();
        //继承的系统模板
        ThingTemplate baseThingTemplate = thingTemplateMapper.selectById(baseThingTemplateStr);
        if (baseThingTemplate == null) {
            log.info("继承模板不能为空");
            return false;
        }
        entity.setThingShape(baseThingTemplate.getThingShape());
        //设置创建时间
        entity.setLastModifiedDate(new Timestamp(new Date().getTime()));
        entity.setOwner(getUsername());
        entity.setType(2001);
        //新建模板
        return retBool(thingTemplateMapper.insert(entity));
    }

    /**
     * 根据名称查询模板
     *
     * @param name
     * @return
     */
    @Override
    public ThingTemplate findByName(String name) {
        return thingTemplateMapper.selectById(name);
    }

    /**
     * 根据类型查询模板
     *
     * @param type
     * @return
     */
    @Override
    public List<String> findByType(Integer type) {
        return thingTemplateMapper.findByType(type);
    }

    /**
     * 根据name删除模板
     * 设备模版有继承的设备不可删除，需要将设备删除后，才能删除设备模版
     *
     * @param name
     */
    @Override
    public Boolean deleteByName(String name) throws JeecgBootException {
        ThingTemplate thingTemplate = thingTemplateMapper.selectById(name);
        if (thingTemplate == null) {
            throw new JeecgBootException(name + " ,模板不存在");
        }
        if (thingTemplate.getType() == 2000) {
            //如果是系统模板，则不能删除
            throw new JeecgBootException(name + " ,系统模板不能删除");
        }
        List<Thing> things = thingMapper.findThingByThingTemplate(name);
        if (things.size() > 0) {
            return false;
        }

        //删除权限表相关数据
        thingTemplateDepartMapper.delete(new QueryWrapper<ThingtemplateDepart>().eq("thingtemplate_id", name));
        roleobjectPermissionMapper.deleteByIdAndTableName(name, THINGTEMPLATE_MODEL);
        userobjectPermissionMapper.deleteByIdAndTableName(name, THINGTEMPLATE_MODEL);
        //删除
        thingTemplateMapper.deleteById(name);
        return true;
    }

    /**
     * <p>修改模板基础信息，根据name</p>
     * <p>不能修改name 和 所继承的模板 </p>
     * <p>不包括扩展属性的修改</p>
     *
     * @param thingTemplate 修改后的模板基础数据
     * @return
     */
    @Override
    public boolean updateByName(ThingTemplate thingTemplate) {
        ThingTemplate entity = thingTemplateMapper.selectById(thingTemplate.getName());
        //修改图标路径
        entity.setAvatar(thingTemplate.getAvatar());
        //修改描述
        entity.setDescription(thingTemplate.getDescription());
        //修改文本描述
        entity.setDocumentationContent(thingTemplate.getDocumentationContent());
        //修改设备主页
        entity.setHomeMashup(thingTemplate.getHomeMashup());
        //修改项目
        entity.setProjectName(thingTemplate.getProjectName());
        //修改标签
        entity.setTags(thingTemplate.getTags());
        //修改继承接口
        entity.setImplementedShapes(thingTemplate.getImplementedShapes());
        //设置修改信息
        setUpdateInfo(thingTemplate);
        return retBool(thingTemplateMapper.updateById(entity));
    }

    /**
     * 根据模板名称，给模板添加属性
     *
     * @param propertyDefinition
     * @param templateName
     */
    @Override
    public void addProperty(PropertyDefinition propertyDefinition, String templateName) {
        //查询模板
        ThingTemplate thingTemplate = thingTemplateMapper.selectById(templateName);
        //设置修改信息
        setUpdateInfo(thingTemplate);
        //获取扩展字段
        String thingShapeStr = thingTemplate.getThingShape();
        ThingShape thingShape = JSON.parseObject(thingShapeStr, ThingShape.class);
        Map<String, PropertyDefinition> propertyDefinitions = thingShape.getPropertyDefinitions();
        int size = propertyDefinitions.size();
        //设置顺序
        propertyDefinition.setOrdinal(size + 1);
        //添加属性
        propertyDefinitions.put(propertyDefinition.getName(), propertyDefinition);
        thingShapeStr = JSON.toJSONString(thingShape);
        thingTemplate.setThingShape(thingShapeStr);
        //保存修改
        thingTemplateMapper.updateById(thingTemplate);
        //给继承模板的设备新增属性
        editThing(templateName, propertyDefinition);
    }


    /**
     * 根据模板名称和属性名，删除可扩展属性
     *
     * @param propertyName
     * @param templateName
     */
    @Override
    public void deleteProperty(String propertyName, String templateName) {
        deletePropertyList(propertyName, templateName);
    }

    /**
     * 根据模板名称， 修改可扩展属性
     *
     * @param propertyDefinition
     * @param templateName
     */
    @Override
    @Transactional
    public void updateProperty(PropertyDefinition propertyDefinition, String templateName) {
        //查询模板
        ThingTemplate thingTemplate = thingTemplateMapper.selectById(templateName);
        //设置修改信息
        setUpdateInfo(thingTemplate);
        //获取属性扩展字段
/*        String thingShapeStr = thingTemplate.getThingShape();
        ThingShape thingShape = JSON.parseObject(thingShapeStr, ThingShape.class);
        Map<String, PropertyDefinition> propertyDefinitions = thingShape.getPropertyDefinitions();
        //修改属性
        propertyDefinitions.put(propertyDefinition.getName(), propertyDefinition);
        thingShapeStr = JSON.toJSONString(thingShape);
        thingTemplate.setThingShape(thingShapeStr);*/
        ThingTemplate thingTemplate1 = thingUtil.addpropertyDefinitions(propertyDefinition, thingTemplate);
        //保存修改
        thingTemplateMapper.updateById(thingTemplate1);
        //修改设备属性
        editThing(templateName, propertyDefinition);
        //thingUtil.editThing(templateName, propertyDefinition,null,null,null,null,null);
    }

    /**
     * 修改继承模板的设备属性,已经存在的则修改，不存在的则新增
     *
     * @param templateName
     * @param propertyDefinition
     */
    private void editThing(String templateName, PropertyDefinition propertyDefinition) {
        List<Thing> thingList = thingMapper.findThingByThingTemplate(templateName);
        String description = propertyDefinition.getDescription();
        String baseType = propertyDefinition.getBaseType();
        String value = (String) propertyDefinition.getValue();
        for (Thing thing : thingList) {
            String thingShapeStr = thing.getThingShape();
            ThingShape thingShape = JSON.parseObject(thingShapeStr, ThingShape.class);
            Map<String, PropertyDefinition> propertyDefinitions = thingShape.getPropertyDefinitions();
            //修改属性
            propertyDefinitions.forEach((k,v)->{
                v.setBaseType(baseType);
                v.setDescription(description);
                v.setValue(value);
            });
            //propertyDefinitions.put(propertyDefinition.getName(), propertyDefinition);
            thingShapeStr = JSON.toJSONString(thingShape);
            thing.setThingShape(thingShapeStr);
            thingMapper.updateById(thing);
        }
    }

    /**
     * 根据用户Id ，查询他拥有查看权限的 物模型模板
     *
     * @param userId
     * @return
     */
    @Override
    public List<ThingTemplate> findByUserId(String userId) {
        boolean admin = permissionService.isAdmin(userId);
        if (admin) {
            //管理员,查询所有
            return thingTemplateMapper.selectList(null);
        } else {
            //查询用户拥有查看权限的 物模型模板的name集合 ，需要添加用户创建的模板
            List<String> nameList = getNameList(userId);
            return thingTemplateMapper.selectBatchIds(nameList);
        }
    }


    /**
     * 条件查询
     *
     * @param templateName
     * @param description
     * @param tags
     * @param projectName
     * @param userId
     * @return
     */
    @Override
    public IPage<ThingTemplate> query(Page page, String templateName, String description, String tags, String projectName, String userId) {
        // 根据条件拼sql
        QueryWrapper<ThingTemplate> queryWrapper = new QueryWrapper<ThingTemplate>();
        if (StringUtils.hasText(templateName)) {
            queryWrapper.like("name", templateName);
        }
        if (StringUtils.hasText(description)) {
            queryWrapper.like("description", description);
        }
        if (StringUtils.hasText(tags)) {
            queryWrapper.like("tags", tags);
        }
        if (StringUtils.hasText(projectName)) {
            queryWrapper.like("projectName", projectName);
        }
        boolean admin = permissionService.isAdmin(userId);
        if (!admin) {
            //  不是管理员，权限过滤 nameList 需要添加用户创建的模板
            List<String> nameList = getNameList(userId);
            if (nameList.size() == 0) {
                return page;
            }
            queryWrapper.in("name", nameList);
        }
        return thingTemplateMapper.selectPage(page, queryWrapper);

    }

    /**
     * 根据设备（物模型实例） 查询物模型模板
     *
     * @param thingName
     * @param userId
     * @return
     */
    @Override
    public ThingTemplate queryByThingName(String thingName, String userId) {
        Thing thing = thingMapper.selectById(thingName);
        if (permissionService.isAdmin(userId)) {
            //管理员不做权限限制
            return thingTemplateMapper.selectById(thing.getThingTemplate());
        } else {
            //  判断模板是否在可查看集合中
            List<String> nameList = getNameList(userId);
            if (nameList.contains(thing.getThingTemplate())) {
                return thingTemplateMapper.selectById(thing.getThingTemplate());
            } else {
                return null;
            }
        }
    }

    /**
     * 根据组织/部门查询模板
     *
     * @param departName
     * @return
     */
    @Override
    public List<ThingTemplate> queryByDepart(String departName) {
        String departIdByDepartName = thingDepartMapper.getDepartIdByDepartName(departName);
        List<String> nameList = thingTemplateDepartMapper.getThingtemplateIdByDepartId(departIdByDepartName);
        return thingTemplateMapper.selectBatchIds(nameList);
    }

    /**
     * 根据模板名称查询对应模板的属性
     *
     * @param thingTemplateName
     * @return
     */
    @Override
    public List<PropertyDefinition> getPropertyList(String thingTemplateName) {
        ThingTemplate thingTemplate = thingTemplateMapper.selectById(thingTemplateName);
        if (thingTemplate == null) {
            return new ArrayList<>();
        }
        String thingShapeStr = thingTemplate.getThingShape();
        ThingShape thingShape = JSON.parseObject(thingShapeStr, ThingShape.class);
        Map<String, PropertyDefinition> propertyDefinitions = thingShape.getPropertyDefinitions();
        Collection<PropertyDefinition> values = propertyDefinitions.values();
        List<PropertyDefinition> propertyDefinitionList = new ArrayList<PropertyDefinition>(values.size());
        propertyDefinitionList.addAll(values);
        return propertyDefinitionList;
    }

    /**
     * 根据模板名称和属性名，删除可扩展属性
     * 批量删除
     *
     * @param propertyNameList 需要删除的属性字符串,以 ','间隔
     * @param templateName
     */
    @Override
    public void deletePropertyList(String propertyNameList, String templateName) {
        if (StringUtils.isEmpty(propertyNameList)) {
            return;
        }
        String[] split = propertyNameList.split(",");
        //查询模板
        ThingTemplate thingTemplate = thingTemplateMapper.selectById(templateName);
        //设置修改信息
        setUpdateInfo(thingTemplate);
        //获取属性扩展字段
        String thingShapeStr = thingTemplate.getThingShape();
        ThingShape thingShape = JSON.parseObject(thingShapeStr, ThingShape.class);
        Map<String, PropertyDefinition> propertyDefinitions = thingShape.getPropertyDefinitions();
        for (String propertyName : split) {
            //删除属性
            propertyDefinitions.remove(propertyName);
        }
        thingShapeStr = JSON.toJSONString(thingShape);
        thingTemplate.setThingShape(thingShapeStr);
        //保存修改
        thingTemplateMapper.updateById(thingTemplate);

        //删除继承模板实例的属性
        List<Thing> thingList = thingMapper.findThingByThingTemplate(templateName);
        for (Thing thing : thingList) {
            setUpdateInfo(thing);
            String thingShape1Str = thing.getThingShape();
            ThingShape thingShape1 = JSON.parseObject(thingShape1Str, ThingShape.class);
            Map<String, PropertyDefinition> propertyDefinitionsThing = thingShape.getPropertyDefinitions();
            for (String propertyName : split) {
                //删除属性
                propertyDefinitionsThing.remove(propertyName);
            }
            thingShapeStr = JSON.toJSONString(thingShape1);
            thing.setThingShape(thingShapeStr);
            thingMapper.updateById(thing);
        }
    }

    /**
     * 获取模板的继承关系
     *
     * @param templateName
     * @param flag         true查所继承的模板，false查继承该模板的设备
     * @return
     */
    @Override
    public JSONObject getTree(String templateName, Boolean flag) {
        JSONObject result = new JSONObject();
        ThingTemplate thingTemplate = thingTemplateMapper.selectById(templateName);
        result.put("name", thingTemplate.getName());
        if (flag) {
            String baseThingTemplate = thingTemplate.getBaseThingTemplate();
            JSONArray jsonArray = new JSONArray();
            JSONObject parent = new JSONObject();
            parent.put("name", baseThingTemplate);
            parent.put("nodes", new JSONArray());
            jsonArray.add(parent);
            result.put("nodes", jsonArray);
        } else {
            //查继承该模板的设备
            List<Thing> list = thingMapper.findThingByThingTemplate(templateName);
            JSONArray jsonArray = new JSONArray();
            for (Thing thing : list) {
                JSONObject children = new JSONObject();
                children.put("name", thing.getName());
                jsonArray.add(children);
            }
            result.put("nodes", jsonArray);
        }
        return result;
    }

    /**
     * 根据token获取当前用户名，如果没有则返回""字符串
     *
     * @return
     */
    private String getUsername() {
        //获取http请求信息
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        String token = TokenUtils.getTokenByRequest(request);
        String username = "";
        if (token != null) {
            //当前用户
            username = JwtUtil.getUsername(token);
        }
        return username;
    }

    /**
     * 给模板设置修改信息（修改者，修改时间）
     */
    private void setUpdateInfo(ThingTemplate thingTemplate) {
        //设置修改时间
        thingTemplate.setLastModifiedDate(new Timestamp(new Date().getTime()));
        //获取http请求信息
        String username = getUsername();
        //添加修改记录
        String configurationChanges = thingTemplate.getConfigurationChanges();
        List<String> list = JSON.parseArray(configurationChanges, String.class);
        if (list == null) {
            list = new LinkedList<>();
        }
        if (list.size() < 20) {
            //小于20条修改记录
            list.add(username);
        } else {
            list.remove(0);
            list.add(username);
        }
        thingTemplate.setConfigurationChanges(JSON.toJSONString(list));
    }

    private void setUpdateInfo(Thing thing) {
        //设置修改时间
        thing.setLastModifiedDate(new Timestamp(new Date().getTime()));
        //获取http请求信息
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        String token = TokenUtils.getTokenByRequest(request);
        String username = "";
        if (token != null) {
            //当前用户
            username = JwtUtil.getUsername(token);
        }
        //添加修改记录
        String configurationChanges = thing.getConfigurationChanges();
        List<String> list = JSON.parseArray(configurationChanges, String.class);
        if (list == null) {
            list = new LinkedList<>();
        }
        if (list.size() < 20) {
            //小于20条修改记录
            list.add(username);
        } else {
            list.remove(0);
            list.add(username);
        }
        thing.setConfigurationChanges(JSON.toJSONString(list));
    }


    /**
     * 查询用户拥有查看权限的 物模型模板的name集合 ，并添加用户创建的模板
     *
     * @param userId
     * @return
     */
    private List<String> getNameList(String userId) {
        //查询用户拥有查看权限的 物模型模板的name集合
        List<String> nameList = getTemplateNameList(userId);
        //查询用户创建的模板
        List<String> usernameList = thingDepartMapper.getUsernameByUserId(userId);
        if (usernameList.size() == 1) {
            List<ThingTemplate> thingTemplateList = thingTemplateMapper.selectList(new QueryWrapper<ThingTemplate>().eq("owner", usernameList.get(0)));
            for (ThingTemplate thingTemplate : thingTemplateList) {
                if (!nameList.contains(thingTemplate.getName())) {
                    nameList.add(thingTemplate.getName());
                }
            }
        }
        return nameList;
    }

    /**
     * 根据用户id，查询所有能查询的模板name集合
     *
     * @param userId
     * @return
     */
    protected List<String> getTemplateNameList(String userId) {
        List<String> departList = permissionService.getDepartList(userId);
        return getTemplateNameList(departList);
    }

    /**
     * 根据部门id集合查询 能查询的模板name
     *
     * @param departList
     * @return
     */
    private List<String> getTemplateNameList(List<String> departList) {
        List<String> thingTemplateNameList = new ArrayList<>();
        if (departList == null || departList.size() == 0) {
            return new ArrayList<String>();
        }
        for (String departId : departList) {
            List<String> thingtemplates = thingTemplateDepartMapper.getThingtemplateIdByDepartId(departId);
            if (thingtemplates.size() > 0) {
                thingTemplateNameList.addAll(thingtemplates);
            }
        }
        //去重
        return thingTemplateNameList.stream().distinct().collect(Collectors.toList());
    }


}
