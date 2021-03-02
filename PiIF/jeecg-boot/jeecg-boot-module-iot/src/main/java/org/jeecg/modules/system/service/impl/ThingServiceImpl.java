package org.jeecg.modules.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.hash.Hash;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.iot.entity.*;
import org.jeecg.modules.iot.mapper.*;
import org.jeecg.modules.system.entity.Thing;
import org.jeecg.modules.system.entity.ThingTemplate;
import org.jeecg.modules.system.mapper.ThingMapper;
import org.jeecg.modules.system.mapper.ThingTemplateMapper;
import org.jeecg.modules.system.model.*;
import org.jeecg.modules.system.service.IPermissionService;
import org.jeecg.modules.system.service.IThingService;
import org.jeecg.modules.system.util.MqttMessageUtil;
import org.jeecg.modules.system.util.PropertyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xduan
 * @version 2020/3/10
 * 物模型实例的接口实现类
 */
@Slf4j
@Service
public class ThingServiceImpl extends ServiceImpl<ThingMapper, Thing> implements IThingService {
    private static final String THINGTABLENAME = "iot_thing_model";

    @Resource
    private ThingMapper thingMapper;

    @Resource
    private ThingTemplateMapper thingTemplateMapper;

    @Resource
    private ThingDepartMapper thingDepartMapper;

    @Resource
    private ThingTemplateDepartMapper thingTemplateDepartMapper;

    @Resource
    private MqttMessageUtil mqttMessageUtil;

    @Resource
    private PropertyUtil propertyUtil;

    @Resource
    private RoleobjectPermissionMapper roleobjectPermissionMapper;

    @Resource
    private UserobjectPermissionMapper userobjectPermissionMapper;

    @Resource
    private AuthPermissionMapper authPermissionMapper;

    @Resource
    private IPermissionService permissionService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 创建物模型实例/设备
     *
     * @param thing
     * @return
     */
    @Override
    @Transactional
    public Thing add(Thing thing, String username) {
        String thingTemplateName = thing.getThingTemplate();
        //继承的模板，属性继承
        ThingTemplate thingTemplate = thingTemplateMapper.selectById(thingTemplateName);
        thing.setThingShape(thingTemplate.getThingShape());
        //设置创建时间
        thing.setLastModifiedDate(new Timestamp(new Date().getTime()));
        thing.setType(2001);
        //设置创建者
        thing.setOwner(username);
        //设置设备编号
        String uuid = UUIDGenerator.generate();
        thing.setIdentifier(uuid);
      /*  前端传
        //不启用
        thing.setEnabled(false);
        //不发布
        thing.setPublished(false);*/
        int insert = thingMapper.insert(thing);
        //继承模板权限
        setPermission(thing);
        return thing;
    }

    /**
     * 给实例设置继承的权限
     *
     * @param thing
     */
    private void setPermission(Thing thing) {
        String thingName = thing.getName();
        String thingTemplate = thing.getThingTemplate();
        //获取可见性所有权限
        List<ThingtemplateDepart> thingtemplateDeparts = thingTemplateDepartMapper.selectList(new QueryWrapper<ThingtemplateDepart>().eq("thingtemplate_id", thingTemplate));
        List<ThingDepart> thingDepartList = new ArrayList<>(thingtemplateDeparts.size());
        for (ThingtemplateDepart thingtemplateDepart : thingtemplateDeparts) {
            String departId = thingtemplateDepart.getDepartId();
            ThingDepart thingDepart = new ThingDepart(thingName, departId);
            //继承权限标识
            thingDepart.setIsExtend(true);
            //继承可见性权限
            thingDepartMapper.insert(thingDepart);
        }

        //继承运行时权限 角色相关的
        List<GuardianRoleobjectpermission> rolePermissionList = roleobjectPermissionMapper.getPermissionListByTemplateName(thingTemplate);
        for (GuardianRoleobjectpermission guardianRoleobjectpermission : rolePermissionList) {
            //将模板权限转化为实例权限
            //获取模板权限Id
            String permissionId = guardianRoleobjectpermission.getPermissionId();
            AuthPermission authPermission = authPermissionMapper.selectById(permissionId);
            //根据表名和codename获取权限id
            List<String> ids = authPermissionMapper.getIdByCodenameAndTableNam(authPermission.getCodename(), THINGTABLENAME);
            if (ids.size() > 0) {
                //设置实例权限id
                guardianRoleobjectpermission.setPermissionId(ids.get(0));
            }
            //设置具体设备实例
            guardianRoleobjectpermission.setObjectPk(thingName);
            //id置空
            guardianRoleobjectpermission.setId(null);
            guardianRoleobjectpermission.setIsExtend(true);
            roleobjectPermissionMapper.insert(guardianRoleobjectpermission);
        }
        //继承运行时权限 用户相关的
        List<GuardianUserobjectpermission> userPermissionList = userobjectPermissionMapper.getPermissionListByTemplateName(thingTemplate);
        for (GuardianUserobjectpermission guardianUserobjectpermission : userPermissionList) {
            //获取模板权限Id
            String permissionId = guardianUserobjectpermission.getPermissionId();
            AuthPermission authPermission = authPermissionMapper.selectById(permissionId);
            //根据表名和codename获取权限id
            List<String> ids = authPermissionMapper.getIdByCodenameAndTableNam(authPermission.getCodename(), THINGTABLENAME);
            if (ids.size() > 0) {
                //设置权限
                guardianUserobjectpermission.setPermissionId(ids.get(0));
            }
            //设置设备实例的权限
            guardianUserobjectpermission.setObjectPk(thingName);
            //id置空
            guardianUserobjectpermission.setId(null);
            guardianUserobjectpermission.setIsExtend(true);
            userobjectPermissionMapper.insert(guardianUserobjectpermission);
        }
    }

    /**
     * 修改物模型实例/设备
     *
     * @param username 用户名
     * @param thing
     */
    @Override
    public Thing update(String username, Thing thing) {
        setUpdateInfo(thing, username);
        thingMapper.updateById(thing);
        return thing;
    }

    /**
     * 删除设备
     *
     * @param name
     */
    @Override
    @Transactional
    public void deleteByName(String name) {
        //删除权限表相关数据
        thingDepartMapper.delete(new QueryWrapper<ThingDepart>().eq("thing_id", name));
        roleobjectPermissionMapper.deleteByIdAndTableName(name, THINGTABLENAME);
        userobjectPermissionMapper.deleteByIdAndTableName(name, THINGTABLENAME);
        //删除设备
        thingMapper.deleteById(name);
        redisUtil.hdel(name);
    }

    /**
     * 添加属性
     *
     * @param propertyDefinition
     * @param thingName
     * @param username
     */
    @Override
    public void addProperty(PropertyDefinition propertyDefinition, String thingName, String username) {
        //查询模板
        Thing thing = thingMapper.selectById(thingName);
        //设置修改信息
        setUpdateInfo(thing, username);
        //获取扩展字段
        String thingShapeStr = thing.getThingShape();
        ThingShape thingShape = JSON.parseObject(thingShapeStr, ThingShape.class);
        Map<String, PropertyDefinition> propertyDefinitions = thingShape.getPropertyDefinitions();
        int size = propertyDefinitions.size();
        //设置顺序
        propertyDefinition.setOrdinal(size + 1);
        //添加属性
        propertyDefinitions.put(propertyDefinition.getName(), propertyDefinition);
        thingShapeStr = JSON.toJSONString(thingShape);
        thing.setThingShape(thingShapeStr);
        //保存修改
        thingMapper.updateById(thing);
        //更新redis属性值
        updatePropertyCache(thingName, propertyDefinition);
    }

    /**
     * 修改属性
     *
     * @param propertyDefinition
     * @param thingName
     * @param username
     */
    @Override
    public void updateProperty(PropertyDefinition propertyDefinition, String thingName, String username) {
        //查询模板
        Thing thing = thingMapper.selectById(thingName);
        //设置修改信息
        setUpdateInfo(thing, username);
        //获取扩展字段
        String thingShapeStr = thing.getThingShape();
        ThingShape thingShape = JSON.parseObject(thingShapeStr, ThingShape.class);
        Map<String, PropertyDefinition> propertyDefinitions = thingShape.getPropertyDefinitions();
        //修改属性
        propertyDefinitions.put(propertyDefinition.getName(), propertyDefinition);
        thingShapeStr = JSON.toJSONString(thingShape);
        thing.setThingShape(thingShapeStr);
        //保存修改
        thingMapper.updateById(thing);
        //更新redis属性值
        updatePropertyCache(thingName, propertyDefinition);
    }

    /**
     * 更新redis属性值
     * @param thingName
     * @param propertyDefinition
     */
    private void updatePropertyCache(String thingName, PropertyDefinition propertyDefinition) {
        Map<String, Object> propertyValueCacheMap = new HashMap<>();
        propertyValueCacheMap.put(propertyDefinition.getName(), propertyDefinition.getValue());
        redisUtil.hmset(thingName, propertyValueCacheMap);
    }

    /**
     * 删除单个属性
     *
     * @param propertyName
     * @param thingName
     * @param username
     */
    @Override
    public void deleteProperty(String propertyName, String thingName, String username) {
        //查询模板
        List<String> propertyNameList = new ArrayList<String>(1);
        propertyNameList.add(propertyName);
        //删除属性
        deletePropertyList(thingName, username, propertyNameList);
    }

    /**
     * 给设备设置修改信息（修改者，修改时间）
     */
    private void setUpdateInfo(Thing thing, String username) {
        //设置修改时间
        thing.setLastModifiedDate(new Timestamp(new Date().getTime()));
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
     * 根据用户Id ，查询他拥有查看权限的 物模型模板
     *
     * @param userId
     * @return
     */
    @Override
    public List<Thing> findByUserId(String userId) {
        boolean admin = permissionService.isAdmin(userId);
        if (admin) {
            //管理员,查询所有
            return thingMapper.selectList(null);
        } else {
            //查询用户拥有查看权限的 物模型模板的name集合 ，需要添加用户创建的模板
            List<String> nameList = getThingNameList(userId);
            return thingMapper.selectBatchIds(nameList);
        }
    }


    /**
     * 条件查询
     *
     * @param thingName
     * @param description
     * @param tags
     * @param projectName
     * @param userId
     * @return
     */
    @Override
    public IPage<Thing> query(Page page, String thingName, String description, String tags, String projectName, String userId) {
        // 根据条件拼sql
        QueryWrapper<Thing> queryWrapper = new QueryWrapper<Thing>();
        if (StringUtils.hasText(thingName)) {
            queryWrapper.like("name", thingName);
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
        return thingMapper.selectPage(page, queryWrapper);

    }

    /**
     * 根据模板查询设备，有可见性权限控制
     *
     * @param templateName
     * @param userId
     * @return
     */
    @Override
    public List<Thing> queryByTemplateName(String templateName, String userId) {
        if (permissionService.isAdmin(userId)) {
            //管理员不做权限限制
            return thingMapper.findThingByThingTemplate(templateName);
        } else {
            //  判断设备是否在可查看集合中
            List<Thing> thingList = thingMapper.findThingByThingTemplate(templateName);
            List<String> nameList = getNameList(userId);
            for (Thing thing : thingList) {
                //如果当前用户不能查看此设备，则移除
                if (!nameList.contains(thing.getName())) {
                    thingList.remove(thing);
                }
            }
            return thingList;
        }
    }

    /**
     * 根据属性名查询设备，有可见性权限控制
     *
     * @param propertyName
     * @param userId
     * @return
     */
    @Override
    public List<Thing> queryByPropertyName(String propertyName, String userId) {
        if (permissionService.isAdmin(userId)) {
            //管理员不做权限限制
            return thingMapper.findThingByPropertyName(propertyName);
        } else {
            //  判断设备是否在可查看集合中
            List<Thing> thingList = thingMapper.findThingByPropertyName(propertyName);
            List<String> nameList = getNameList(userId);
            //如果当前用户不能查看此设备，则在设备列表中移除
            thingList.removeIf(thing -> !nameList.contains(thing.getName()));
            return thingList;
        }
    }

    /**
     * 根据设备查询所有属性（完整数据）
     *
     * @param thingName
     * @return
     */
    @Override
    public List<PropertyDefinition> getPropertyList(String thingName) throws Exception {
        Thing thing = thingMapper.selectById(thingName);
        if (thing == null) {
            return new ArrayList<>();
        }
        ThingShape thingShape = getThingShape(thing);
        Map<String, PropertyDefinition> propertyDefinitions = thingShape.getPropertyDefinitions();
        Collection<PropertyDefinition> values = propertyDefinitions.values();
        List<PropertyDefinition> propertyDefinitionList = new ArrayList<PropertyDefinition>(values.size());
        //当前设备对应的唯一字段，即数据库表名
        String identifier = thing.getIdentifier();
        //数据键值对对象
        Map propertyValue = getPropertyValue(identifier);
        for (PropertyDefinition propertyDefinition : values) {
            //处理属性值
            setPropertyValue(propertyValue, propertyDefinition);
            propertyDefinitionList.add(propertyDefinition);
        }
        return propertyDefinitionList;
    }

    /**
     * 对属性进行属性值的处理
     *
     * @param propertyValue
     * @param propertyDefinition
     */
    private void setPropertyValue(Map propertyValue, PropertyDefinition propertyDefinition) {
        if (propertyValue.isEmpty()) {
            return;
        }
        //数据处理 
        Boolean isLocalOnly = propertyDefinition.getIsLocalOnly();
        if (!isLocalOnly) {
            //不是本地属性，即属性绑定了远程数据库字段，需要处理属性值数据
            Object value = propertyValue.get(propertyDefinition.getRouteColumn());
            if (value == null) {
                value = "";
            }
            propertyDefinition.setValue(value.toString());
        }
    }

    /**
     * 根据设备查询属性及属性值
     *
     * @param thingName
     * @return
     */
    @Override
    public Map<String, String> getPropertyValueList(String thingName) throws Exception {
        Thing thing = thingMapper.selectById(thingName);
        ThingShape thingShape = getThingShape(thing);
        //扩展属性字段
        Map<String, PropertyDefinition> propertyDefinitions = thingShape.getPropertyDefinitions();
        //返回结果
        Map<String, String> map = new HashMap<>();
        //数据键值对对象
        Map propertyValue = getPropertyValue(thing.getIdentifier());
        //遍历属性，并处理数据
        for (PropertyDefinition propertyDefinition : propertyDefinitions.values()) {
            //数据处理  处理属性值
            setPropertyValue(propertyValue, propertyDefinition);
            map.put(propertyDefinition.getName(), (String) propertyDefinition.getValue());
        }
        return map;
    }

    /**
     * 根据设备名称和属性名称（多个），返回（name+value）
     *
     * @param thingName
     * @param propertyNameList
     * @return
     */
    @Override
    public Map<String, String> getPropertyValueByPropertyName(String thingName, List<String> propertyNameList) throws Exception {
        Thing thing = thingMapper.selectById(thingName);
        ThingShape thingShape = getThingShape(thing);
        Map<String, PropertyDefinition> propertyDefinitions = thingShape.getPropertyDefinitions();
        Map<String, String> map = new HashMap<>();
        //当前设备对应的唯一字段，即数据库表名
        String identifier = thing.getIdentifier();
        //数据键值对对象
        Map propertyValue = getPropertyValue(identifier);
        //遍历需要的属性，并处理数据
        for (String propertyName : propertyNameList) {
            PropertyDefinition propertyDefinition = propertyDefinitions.get(propertyName);
            if (propertyDefinition == null) {
                continue;
            }
            //数据处理 ，处理属性值
            setPropertyValue(propertyValue, propertyDefinition);
            map.put(propertyDefinition.getName(), (String) propertyDefinition.getValue());
        }
        return map;
    }

    /**
     * 根据identifier获取对应表的所有字段的键值对
     *
     * @param identifier
     * @return
     * @throws Exception
     */
    private Map getPropertyValue(String identifier) throws Exception {
        //拼接表名
        String tableName = "iotgateway/" + identifier;
        //测试使用表名：正式测试时需要注释
        // tableName = "iotgateway/first";
        Result<Object> messageByTableName = null;
        try {
            messageByTableName = mqttMessageUtil.findMessageByTableName(tableName, 1);
        } catch (Exception e) {
            log.error(e.toString());
        }
        if (messageByTableName == null) {
            //返回空map
            return new HashMap();
        }
        JSONArray message = (JSONArray) messageByTableName.getResult();
        Object obj = message.get(0);
        Map messageMap = null;
        if (obj instanceof Map) {
            messageMap = (Map) obj;
        }
        return messageMap;
    }

    /**
     * 根据设备名称+属性名称（多个）修改对应的值
     *
     * @param thingName
     * @param propertyNameValue
     * @param username
     */
    @Override
    public void updatePropertyValue(String thingName, String username, Map<String, String> propertyNameValue) throws Exception {
        Thing thing = thingMapper.selectById(thingName);
        if (thing == null) {
            throw new Exception(thingName + "不存在");
        }
        setUpdateInfo(thing, username);
        ThingShape thingShape = getThingShape(thing);
        Map<String, PropertyDefinition> propertyDefinitions = thingShape.getPropertyDefinitions();
        for (String propertyName : propertyNameValue.keySet()) {
            //获取属性
            PropertyDefinition propertyDefinition = propertyDefinitions.get(propertyName);
            if (propertyDefinition == null) {
                throw new Exception(propertyName + " 不存在。");
            }
            //修改值
            propertyDefinition.setValue(propertyNameValue.get(propertyName));
            String msg = propertyUtil.checkValue(propertyDefinition);
            if (StringUtils.hasText(msg)) {
                throw new Exception(msg);
            }
        }
        thing.setThingShape(JSON.toJSONString(thingShape));
        thingMapper.updateById(thing);
    }

    /**
     * 根据设备名称+属性名称（多个）删除属性
     *
     * @param thingName
     * @param username
     * @param propertyNameList
     */
    @Override
    public void deletePropertyList(String thingName, String username, List<String> propertyNameList) {
        Thing thing = thingMapper.selectById(thingName);
        setUpdateInfo(thing, username);
        ThingShape thingShape = getThingShape(thing);
        Map<String, PropertyDefinition> propertyDefinitions = thingShape.getPropertyDefinitions();//属性

        for (String propertyName : propertyNameList) {
            //移除属性
            propertyDefinitions.remove(propertyName);

        }
        deleteAuxiliary(thingShape, propertyNameList);
        thing.setThingShape(JSON.toJSONString(thingShape));
        thingMapper.updateById(thing);
        //删除redis中的设备属性值
        for (String propertyName : propertyNameList) {
            redisUtil.hdel(thingName,propertyName);
        }
    }

    /**
     * 获取设备属性，分成设备属性和继承的模板属性。如果redis中有设备属性，则获取redis中的设备属性值；如果没有，则取数据库中的值
     *
     * @param thingName
     * @throws Exception
     */
    @Override
    public JSONArray getPropertyList2(String thingName) throws Exception {
        //获取绑定属性
        List<PropertyDefinition> propertyList = getPropertyList(thingName);
        Thing thing = thingMapper.selectById(thingName);
        String thingTemplateName = thing.getThingTemplate();
        ThingTemplate thingTemplate = thingTemplateMapper.selectById(thingTemplateName);
        ThingShape thingTemplateShape = JSON.parseObject(thingTemplate.getThingShape(), ThingShape.class);
        Map<String, PropertyDefinition> propertyDefinitions = thingTemplateShape.getPropertyDefinitions();
        Set<String> strings = propertyDefinitions.keySet();
        List<PropertyDefinition> thingPropertyList = new ArrayList<>();
        List<PropertyDefinition> thingTemplatePropertyList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray();
        //redis中有设备属性值
        if (redisUtil.hasKey(thingName)){
            //从redis获取设备属性值
            Map<Object, Object> propertyValueCacheMap = redisUtil.hmget(thingName);
            if (!propertyValueCacheMap.isEmpty()){
                for (PropertyDefinition propertyDefinition : propertyList) {
                    if (strings.contains(propertyDefinition.getName())) {
                        //继承的模板属性
                        thingTemplatePropertyList.add(propertyDefinition);
                    } else {
                        //用缓存中的值替换数据库中的值
                        propertyValueCacheMap.forEach((k, v) -> {
                            if (propertyDefinition.getName().equals(k)) {
                                propertyDefinition.setValue(String.valueOf(v));
                            }
                        });
                        //设备属性
                        thingPropertyList.add(propertyDefinition);
                    }
                }
                jsonArray.add(thingPropertyList);
                jsonArray.add(thingTemplatePropertyList);
            }
        }else { //redis中没有属性缓存
            for (PropertyDefinition propertyDefinition : propertyList) {
                if (strings.contains(propertyDefinition.getName())) {
                    //继承的模板属性
                    thingTemplatePropertyList.add(propertyDefinition);
                } else {
                    //设备属性
                    thingPropertyList.add(propertyDefinition);
                }
            }
            jsonArray.add(thingPropertyList);
            jsonArray.add(thingTemplatePropertyList);

        }
        return jsonArray;
    }

    /**
     * 查询当前的设备的继承树结构
     *
     * @param thingName
     */
    @Override
    public NodeTreeModel getTree(String thingName) {
        Thing thing = thingMapper.selectById(thingName);
        //设备继承的模板
        ThingTemplate thingTemplate = thingTemplateMapper.selectById(thing.getThingTemplate());
        NodeTreeModel treeModel = new NodeTreeModel();
        treeModel.setName(thingName);
        List<NodeTreeModel> parents = new ArrayList<>();
        //第一层 继承的 数据
        NodeTreeModel parent1 = new NodeTreeModel();
        parent1.setName(thingTemplate.getName());
        //将第二层parent添加进第一层中
        List<NodeTreeModel> parents2 = new ArrayList<>();
        //第二层继承的数据
        NodeTreeModel parent2 = new NodeTreeModel();
        parent2.setName(thingTemplate.getBaseThingTemplate());
        parent2.setNodes(new ArrayList<>());
        parents2.add(parent2);
        parent1.setNodes(parents2);
        //将第一层parent添加进结果
        parents.add(parent1);
        treeModel.setNodes(parents);

        return treeModel;
    }

    /**
     * 获取对应设备的扩展字段，并且转换为 thingShape对象
     *
     * @param thing
     * @return
     */
    private ThingShape getThingShape(Thing thing) {
        String thingShapeStr = thing.getThingShape();
        return JSON.parseObject(thingShapeStr, ThingShape.class);
    }

    /**
     * 根据部门id集合查询 能查询的设备name
     *
     * @param departList
     * @return
     */
    private List<String> getThingNameList(List<String> departList) {
        List<String> thingNameList = new ArrayList<>();
        if (departList == null || departList.size() == 0) {
            return thingNameList;
        }
        for (String departId : departList) {
            List<String> thingNames = thingDepartMapper.getThingIdByDepartId(departId);
            if (thingNames.size() > 0) {
                thingNameList.addAll(thingNames);
            }
        }
        //去重
        return thingNameList.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 查询用户拥有查看权限的 物模型设备的name集合 ，并添加用户创建的设备
     *
     * @param userId
     * @return
     */
    private List<String> getNameList(String userId) {
        //查询用户拥有查看权限的 物模型的name集合
        List<String> nameList = getThingNameList(userId);
        //查询用户创建的设备
        List<String> usernameList = thingDepartMapper.getUsernameByUserId(userId);
        if (usernameList.size() == 1) {
            List<Thing> thingList = thingMapper.selectList(new QueryWrapper<Thing>().eq("owner", usernameList.get(0)));
            for (Thing thing : thingList) {
                if (!nameList.contains(thing.getName())) {
                    nameList.add(thing.getName());
                }
            }
        }
        return nameList;
    }

    /**
     * 根据用户id，查询所有能查询的设备name集合
     *
     * @param userId
     * @return
     */
    private List<String> getThingNameList(String userId) {
        List<String> departList = permissionService.getDepartList(userId);
        return getThingNameList(departList);
    }

    /*
     * @Description:删除属性后属性对应的绑定数据也被删除
     * @Author: zhj
     * @Date: 2020/9/21 14:22
     */
    private ThingShape deleteAuxiliary(ThingShape thingShape, List<String> propertyNameList) {

        Map<String, EventDefinition> eventDefinitions = thingShape.getEventDefinitions();//事件
        Map<String, Subscription> subscriptions = thingShape.getSubscriptions();//订阅
        //事件key
        List<String> serialNumber = new ArrayList<>();

        //删除所有属性的事件的json并获取唯一标识
        try {
            eventDefinitions.forEach((k, v) -> {
                if (propertyNameList.stream().anyMatch(m -> m.equals(v.getPropertyName()))) {
                    serialNumber.add(k);
                    eventDefinitions.remove(k);
                }
            });

            //删除所以事件对应的对应的json数据
            subscriptions.forEach((k, v) -> {
                if (serialNumber.stream().anyMatch(m -> m.equals(v.getEventName()))) {
                    subscriptions.remove(k);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            //mull异常
            e.getMessage();
        }

        return thingShape;
    }

}


