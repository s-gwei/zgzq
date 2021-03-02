package org.jeecg.modules.system.model;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author xduan
 * @version 2020/3/4
 * @desc 可扩展字段json模板对象
 */
@Data
public class ThingShape  {
    /**
     * 属性扩展字段
     */
    private Map<String,PropertyDefinition> propertyDefinitions = new LinkedHashMap<>();
    /**
     * 订阅扩展字段
     */
    private Map<String,Subscription> subscriptions = new LinkedHashMap<>();
    /**
     * 事件扩展字段
     */
    private Map<String,EventDefinition> eventDefinitions = new LinkedHashMap<>();
    private Map<String,Object> serviceImplementations = new LinkedHashMap<>();
    private Map<String,Object> serviceMappings = new LinkedHashMap<>();
    private Map<String,Object> serviceDefinitions = new LinkedHashMap<>();


}
