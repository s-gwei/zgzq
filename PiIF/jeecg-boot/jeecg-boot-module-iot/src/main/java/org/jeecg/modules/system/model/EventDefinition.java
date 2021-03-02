package org.jeecg.modules.system.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhang ho jian
 * @date 2020/9/1
 * @time 9:59
 * @Description : 事件定义模型
 */
@Data
public class EventDefinition implements Serializable {

    private static final long serialVersionUID = -5685861685995424666L;

    //唯一标识由属性名和值拼接成，避免同一个属性出现多个相同的事件
    private String serialNumber;
    //事件名
    private String name;
    //属性名
    private String propertyName;
    //描述
    private String description;
    //事件的值(规制的值)
    private String operatorSchema;
    //分类
    private String category;
    //排序
    private Integer ordinal;
    //是否开启(0关闭，1启动)
    private String whetherStart;
    //事件规则
    private String rule;

}
