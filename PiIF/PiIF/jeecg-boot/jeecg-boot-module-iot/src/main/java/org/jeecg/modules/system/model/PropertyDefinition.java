package org.jeecg.modules.system.model;

import lombok.Data;


import java.io.Serializable;

/**
 * @author xduan
 * @version 2020/3/4
 * @desc 属性定义模型
 */
@Data
public class PropertyDefinition implements Serializable {
    private static final long serialVersionUID = -5685861685995424666L;

    private String sourceType = "Unknown";
    //属性名
    private String name;
    //属性值
    private String value = "";
    //
    private Aspects aspects = new Aspects();
    //属性值是否来自本地，true来自本地，false来自其他数据库，需要处理
    private Boolean isLocalOnly = true;
    //属性绑定字段，列名来自于数据采集接口
    private String routeColumn;
    //描述
    private String description;
    //
    private String sourceName = "";
    //是否具备默认值
    private Boolean defaultValue;
    //分类
    private String category;
    //基础类型
    private String baseType;
    //排序
    private Integer ordinal;
}
