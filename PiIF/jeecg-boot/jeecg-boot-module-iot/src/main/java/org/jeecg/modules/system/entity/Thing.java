package org.jeecg.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author xduan
 * @version 2020/3/4
 * @desc 物模型 的实体类
 */
@Data
@TableName("iot_thing_model")
public class Thing implements Serializable {

    private static final long serialVersionUID = -7395025280807559829L;

    //报警监控配置 json
    @TableField("alertConfigurations")
    private String alertConfigurations;
    //icon
    @TableField("avatar")
    private String avatar;
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
    //是否启用 0：禁用 1：启用, 默认禁用'
    @TableField("enabled")
    private Boolean enabled =false;
    //设备主页
    @TableField("homeMashup")
    private String homeMashup;
    //uuid生成
    @TableField("identifier")
    private String identifier;
    //接口继承
    @TableField("implementedShapes")
    private String implementedShapes;

    //上次修改时间
    @TableField("lastModifiedDate")
    private Date lastModifiedDate;
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
    //是否发布，0:未发布，1：已发布 默认未发布
    @TableField("published")
    private Boolean published = false; 
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
    //标签
    @TableField("tags")
    private String tags;
    //远程绑定id
    @TableField("tenantId")
    private String tenantId;
    //可扩展字段 json
    @TableField("thingShape")
    private String thingShape;
    //继承的模板名称
    @TableField("thingTemplate")
    private String thingTemplate;
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
