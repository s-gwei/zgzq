package org.jeecg.modules.system.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.system.entity.ThingTemplate;
import org.jeecg.modules.system.model.PropertyDefinition;

import java.util.List;

/**
 * @author xduan
 * @version 2020/3/5
 * 物模型模板的服务接口
 */
public interface IThingTemplateService extends IService<ThingTemplate> {
    /**
     * 根据名称查询模板
     *
     * @param name
     * @return
     */
    ThingTemplate findByName(String name);

    /**
     * 根据类型查询模板
     *
     * @param type
     * @return
     */
    List<String> findByType(Integer type);

    /**
     * 根据name删除
     *
     * @param name
     */
    Boolean deleteByName(String name) throws JeecgBootException;

    /**
     * <p>修改模板基础信息，根据name</p>
     * <p>不能修改name 和 所继承的模板 </p>
     * <p>不包括扩展属性的修改</p>
     *
     * @param thingTemplate
     * @return
     */
    boolean updateByName(ThingTemplate thingTemplate);

    /**
     * 根据模板名称，给模板添加属性
     *
     * @param propertyDefinition
     * @param templateName
     */
    void addProperty(PropertyDefinition propertyDefinition, String templateName);

    /**
     * 根据模板名称和属性名，删除可扩展属性
     *
     * @param propertyName
     * @param templateName
     */
    void deleteProperty(String propertyName, String templateName);

    /**
     * 根据模板名称， 修改可扩展属性
     *
     * @param propertyDefinition
     * @param templateName
     */
    void updateProperty(PropertyDefinition propertyDefinition, String templateName);

    /**
     * 根据用户Id ，查询他拥有查看权限的 物模型模板
     *
     * @param userId
     * @return
     */
    List<ThingTemplate> findByUserId(String userId);

    /**
     * 条件查询
     *
     *
     * @param page
     * @param templateName
     * @param description
     * @param tags
     * @param projectName
     * @param userId
     * @return
     */
    IPage<ThingTemplate> query(Page page, String templateName, String description, String tags, String projectName, String userId);

    /**
     * 根据设备（物模型实例） 查询物模型模板
     *
     * @param thingName
     * @param userId
     * @return
     */
    ThingTemplate queryByThingName(String thingName, String userId);

    /**
     * 根据组织/部门查询模板
     *
     * @param departName
     *
     * @return
     */
    List<ThingTemplate> queryByDepart(String departName);

    /**
     * 根据模板名称查询对应模板的属性
     * @param thingTemplateName
     * @return
     */
    List<PropertyDefinition> getPropertyList(String thingTemplateName);

    /**
     * 根据模板名称和属性名，删除可扩展属性
     *批量删除
     * @param propertyNameList
     * @param templateName
     */
    void deletePropertyList(String propertyNameList, String templateName);

    /**
     * 获取模板的继承关系
     * @param templateName
     * @param flag  true查所继承的模板，false查继承该模板的设备
     * @return
     */
    JSONObject getTree(String templateName, Boolean flag);
}
