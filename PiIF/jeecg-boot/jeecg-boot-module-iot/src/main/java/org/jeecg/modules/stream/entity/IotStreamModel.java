package org.jeecg.modules.stream.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author:wzp
 * @Date:Created in 3:51 PM 2020/4/9
 * @Description:IotStreamModel
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IotStreamModel implements Serializable {
    /**
     * 主键id
     */
    private int id;

    /**
     * 名称
     */
    private String name;

    /**
     * 模型描述
     */
    private String description;

    /**
     * 项目
     */
    private String projectname;

    /**
     * 标签
     */
    private String tags;

    /**
     * 实现形状
     */
    private String implementedshapes;

    /**
     * 物模板
     */
    private String thingtemplate;

    /**
     * 活跃状态
     */
    private boolean enabled;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 列信息
     */
    private String fielddefinitions;

    /**
     * 修改信息
     */
    private Object configurationchanges;

    /**
     * 更新时间
     */
    private Date lastmodifieddate;

    /**
     * 识别
     */
    private String identifier;

    /**
     * 使用说明
     */
    private String documentationcontent;

    /**
     * 发行
     */
    private boolean published;

    /**
     * 价值流
     */
    private String valuestream;

    /**
     * 主页
     */
    private String homemashup;

    /**
     * 数据形状
     */
    private String datashape;

    private String designtimepermissions;

    private String classname;

    private String configurationtables;

    private String alertconfigurations;

    private String owner;

    private String propertybindings;

    private String remoteeventbindings;

    private String remotepropertybindings;

    private String remoteservicebindings;

    private String runtimepermissions;

    private String tenantid;

    private String thingshape;

    private Integer type;

    private String visibilitypermissions;

    /**
     * 权限相关uuid
     */
    private String objectPk;

    @TableField(exist = false)
    private String object_pk;

    private static final long serialVersionUID = 1L;
}