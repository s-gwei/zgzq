package org.jeecg.modules.system.util;

import com.alibaba.fastjson.JSON;
import org.jeecg.modules.system.entity.Thing;
import org.jeecg.modules.system.entity.ThingTemplate;
import org.jeecg.modules.system.mapper.ThingMapper;
import org.jeecg.modules.system.model.EventDefinition;
import org.jeecg.modules.system.model.PropertyDefinition;
import org.jeecg.modules.system.model.Subscription;
import org.jeecg.modules.system.model.ThingShape;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author zhang ho jian
 * @date 2020/9/3
 * @time 10:54
 * @Description : 模板工具类
 */
@Component
public class ThingUtil {

    @Autowired
    private ThingMapper thingMapper;

    /**
     * 属性
     * @param propertyDefinition
     * @param thingTemplate
     * @return
     */
    public ThingTemplate addpropertyDefinitions(PropertyDefinition propertyDefinition, ThingTemplate thingTemplate){

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

        return thingTemplate;
    }

    /**
     * 事件
     * @param eventDefinition
     * @param thingTemplate
     * @return
     */
    public ThingTemplate addIncidentDefinitions(EventDefinition eventDefinition, ThingTemplate thingTemplate){

        //获取扩展字段
        String thingShapeStr = thingTemplate.getThingShape();
        ThingShape thingShape = JSON.parseObject(thingShapeStr, ThingShape.class);
        Map<String, EventDefinition> eventDefinitions = thingShape.getEventDefinitions();

        int size = eventDefinitions.size();
        //设置顺序
        eventDefinition.setOrdinal(size + 1);
        //添加事件
        eventDefinitions.put(eventDefinition.getName(), eventDefinition);

        thingShapeStr = JSON.toJSONString(thingShape);
        thingTemplate.setThingShape(thingShapeStr);

        return thingTemplate;
    }

    /**
     *
     * @param templateName  模板名称
     * @param propertyDefinition  属性封装类
     * @param eventDefinition    事件封装类
     * @param subscription       订阅封装类
     * @param serviceImplementation    服务实现类
     * @param serviceMapping      服务定义类
     * @param serviceDefinition   服务实行类
     */
    public void editThing(String templateName, PropertyDefinition propertyDefinition ,EventDefinition eventDefinition , Subscription subscription , Object serviceImplementation , Object serviceMapping , Object serviceDefinition) {
        List<Thing> thingList = thingMapper.findThingByThingTemplate(templateName);
        for (Thing thing : thingList) {
            String thingShapeStr = thing.getThingShape();
            ThingShape thingShape = JSON.parseObject(thingShapeStr, ThingShape.class);
            Map<String, PropertyDefinition> propertyDefinitions = thingShape.getPropertyDefinitions();
            Map<String, Subscription> subscriptions = thingShape.getSubscriptions();
            Map<String, EventDefinition> eventDefinitions = thingShape.getEventDefinitions();
            Map<String, Object> serviceImplementations = thingShape.getServiceImplementations();
            Map<String, Object> serviceMappings = thingShape.getServiceMappings();
            Map<String, Object> serviceDefinitions = thingShape.getServiceDefinitions();
            //修改属性
            if (!checkObjAllFieldsIsNull(propertyDefinition)){propertyDefinitions.put(propertyDefinition.getName(), propertyDefinition);}
            if (!checkObjAllFieldsIsNull(subscription)){subscriptions.put(subscription.getName(),subscription);}
            if (!checkObjAllFieldsIsNull(eventDefinition)){eventDefinitions.put(eventDefinition.getName(),eventDefinition);}
            if (!checkObjAllFieldsIsNull(serviceImplementation)){serviceImplementations.put("预留字段",serviceImplementation);}
            if (!checkObjAllFieldsIsNull(serviceMapping)){serviceMappings.put("预留字段",serviceMapping);}
            if (!checkObjAllFieldsIsNull(serviceDefinition)){serviceDefinitions.put("预留字段",serviceDefinition);}
            thingShapeStr = JSON.toJSONString(thingShape);
            thing.setThingShape(thingShapeStr);
            thingMapper.updateById(thing);
        }
    }

    private static boolean checkObjAllFieldsIsNull(Object object) {
        if (null == object) {
            return true;
        }

        try {
            for (Field f : object.getClass().getDeclaredFields()) {
                f.setAccessible(true);

                System.out.print(f.getName() + ":");
                System.out.println(f.get(object));

                if (f.get(object) != null && StringUtils.isNotBlank(f.get(object).toString())) {
                    return false;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }


}
