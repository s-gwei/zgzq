package org.jeecg.modules.valuestream.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * @Author:wzp
 * @Date:Created in 4:32 PM 2020/4/9
 * @Description:IotValuestreamModel 价值流
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IotValuestreamModel implements Serializable {
    private Integer entityId;

    /**
     * 名称
     */
    private String name;

    /**
     * 列信息
     */
    private String fielddefinitions;

    /**
     * 修改信息
     */
    private String configurationchanges;

    /**
     * 表描述
     */
    private String description;

    /**
     * 使用说明
     */
    private String documentationcontent;

    /**
     * 活跃状态
     */
    private boolean enabled;

    /**
     * 主页
     */
    private String homemashup;

    /**
     * 识别
     */
    private String identifier;

    /**
     * 实现形状
     */
    private String implementedshapes;

    /**
     * 修改/创建时间
     */
    private Date lastmodifieddate;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 项目
     */
    private String projectname;

    /**
     * 权限相关uuid
     */
    private String objectPk;

    /**
     * 是否发布
     */
    private boolean published;

    /**
     * 标签
     */
    private String tags;

    /**
     * 物模板
     */
    private String thingtemplate;

    /**
     * 价值流
     */
    private String valuestream;

    /**
     * 返回前端的表相关信息
     */
    @TableField(exist = false)
    private Map tableMsg;

    /**
     * 返回前端的列信息
     */
    @TableField(exist = false)
    private Collection columnMsg;

    @TableField(exist = false)
    private String object_pk;

    private String configurationtables;

    private String alertconfigurations;

    private String owner;

    private String designtimepermissions;

    private String classname;

    private String propertybindings;

    private String remoteeventbindings;

    private String remotepropertybindings;

    private String remoteservicebindings;

    private String runtimepermissions;

    private String tenantid;

    private String thingshape;

    private Integer type;

    private String visibilitypermissions;

    private static final long serialVersionUID = 1L;
}