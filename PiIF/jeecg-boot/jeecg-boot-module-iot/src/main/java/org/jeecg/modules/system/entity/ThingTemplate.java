package org.jeecg.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


import java.io.Serializable;


import java.sql.Timestamp;


/**
 * @author xduan
 * @version 2020/3/3
 * @desc 物模型模板实体类
 */
@Data
@TableName("iot_thingtemplate_model")
public class ThingTemplate implements Serializable {
    private static final long serialVersionUID = -135180260097884507L;

    //报警监控配置 json
    @TableField("alertConfigurations")
    private String alertConfigurations;
    //图片 路径
    @TableField("avatar")
    private String avatar;
    // 继承的模板
    @TableField("baseThingTemplate")
    private String baseThingTemplate;
    //类名
    @TableField("className")
    private String className;
    //记录谁进行了修改操作。默认20条记录 json
    @TableField("configurationChanges")
    private String configurationChanges;
    //数据字典，用于配置一些特殊的属性
    @TableField("configurationTables")
    private String configurationTables;
    //描述
    @TableField("description")
    private String description;
    //权限
    @TableField("designTimePermissions")
    private String designTimePermissions;
    //设备的文本描述
    @TableField("documentationContent")
    private String documentationContent;
    //设备主页
    @TableField("homeMashup")
    private String homeMashup;
    //接口继承
    @TableField("implementedShapes")
    private String implementedShapes;
    //权限
    @TableField("instanceDesignTimePermissions")
    private String instanceDesignTimePermissions;
    //权限
    @TableField("instanceRunTimePermissions")
    private String instanceRunTimePermissions;
    //权限
    @TableField("instanceVisibilityPermissions")
    private String instanceVisibilityPermissions;
    //上次修改时间
    @TableField("lastModifiedDate")
    private Timestamp lastModifiedDate;
    //模板名称 唯一标识
    @TableField("name")
    @TableId(type = IdType.NONE)
    private String name;
    //拥有者/创建者
    @TableField("owner")
    private String owner;
    //项目名称
    @TableField("projectName")
    private String projectName;
    //远程字段
    @TableField("propertyBindings")
    private String propertyBindings;
    //远程字段
    @TableField("remoteEventBindings")
    private String remoteEventBindings;
    //远程字段
    @TableField("remotePropertyBindings")
    private String remotePropertyBindings;
    //远程字段
    @TableField("remoteServiceBindings")
    private String remoteServiceBindings;
    //远程字段
    @TableField("runTimePermissions")
    private String runTimePermissions;
    //远程字段
    @TableField("sharedConfigurationTables")
    private String sharedConfigurationTables;
    //标签
    @TableField("tags")
    private String tags;
    //远程绑定id
    @TableField("tenantId")
    private String tenantId;
    //包名
    @TableField("thingPackage")
    private String thingPackage;
    //可扩展字段 json
    @TableField("thingShape")
    private String thingShape;
    //类型
    @TableField("type")
    private Integer type;
    //存储库
    @TableField("valueStream")
    private String valueStream;
    //权限
    @TableField("visibilityPermissions")
    private String visibilityPermissions;
}
