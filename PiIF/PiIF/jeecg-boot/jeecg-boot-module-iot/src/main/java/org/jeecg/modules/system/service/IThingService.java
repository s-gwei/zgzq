package org.jeecg.modules.system.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.system.entity.Thing;
import org.jeecg.modules.system.model.NodeTreeModel;
import org.jeecg.modules.system.model.PropertyDefinition;

import java.util.List;
import java.util.Map;

/**
 * @author xduan
 * @version 2020/3/10
 * 物模型实例服务接口层
 */
public interface IThingService extends IService<Thing> {
    /**
     * 创建物模型实例/设备
     *
     * @param thing
     * @param username
     * @return
     */
    Thing add(Thing thing, String username);

    /**
     * 修改物模型实例/设备
     *
     * @param username
     * @param thing
     */
    Thing update(String username, Thing thing);

    /**
     * 删除设备
     *
     * @param name
     */
    void deleteByName(String name);

    /**
     * 添加属性
     *
     * @param propertyDefinition
     * @param thingName
     * @param username
     */
    void addProperty(PropertyDefinition propertyDefinition, String thingName, String username);

    /**
     * 修改属性
     *
     * @param propertyDefinition
     * @param thingName
     * @param username
     */
    void updateProperty(PropertyDefinition propertyDefinition, String thingName, String username);

    /**
     * 删除属性
     *
     * @param propertyName
     * @param thingName
     * @param username
     */
    void deleteProperty(String propertyName, String thingName, String username);

    /**
     * 查询所有设备，带权限控制
     *
     * @param userId
     * @return
     */
    List<Thing> findByUserId(String userId);

    /**
     * 条件查询
     *
     *
     * @param page
     * @param thingName
     * @param description
     * @param tags
     * @param projectName
     * @param userId
     * @return
     */
    IPage<Thing> query(Page page, String thingName, String description, String tags, String projectName, String userId);

    /**
     * 根据模板名查询设备
     *
     * @param templateName
     * @param userId
     * @return
     */
    List<Thing> queryByTemplateName(String templateName, String userId);

    /**
     * 根据属性名查询设备
     *
     * @param propertyName
     * @param userId
     * @return
     */
    List<Thing> queryByPropertyName(String propertyName, String userId);

    /**
     * 根据设备查询所有属性（完整数据）
     *
     * @param thingName
     * @return
     */
    List<PropertyDefinition> getPropertyList(String thingName) throws Exception;

    /**
     * 根据设备查询属性及属性值
     *
     * @param thingName
     * @return
     */
    Map<String, String> getPropertyValueList(String thingName) throws Exception;

    /**
     * 根据设备名称和属性名称（多个），返回（name+value）
     *
     * @param thingName
     * @param propertyNameList
     * @return
     */
    Map<String, String> getPropertyValueByPropertyName(String thingName, List<String> propertyNameList) throws Exception;

    /**
     * 根据设备名称+属性名称（多个）修改对应的值
     *
     * @param thingName
     * @param username
     * @param propertyNameValue
     */
    void updatePropertyValue(String thingName, String username, Map<String, String> propertyNameValue) throws Exception;

    /**
     * 根据设备名称+属性名称（多个）删除属性
     * @param thingName
     * @param username
     * @param propertyNameList
     */
    void deletePropertyList(String thingName, String username, List<String> propertyNameList);

    JSONArray getPropertyList2(String thingName) throws Exception;

    NodeTreeModel getTree(String thingName);
}
