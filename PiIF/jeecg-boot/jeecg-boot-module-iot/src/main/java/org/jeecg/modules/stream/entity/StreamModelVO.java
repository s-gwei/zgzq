package org.jeecg.modules.stream.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author:wzp
 * @Date:Created in 3:51 PM 2020/4/9
 * @Description:前端交互StreamModelVO
 */
@Data
public class StreamModelVO implements Serializable {
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
     * 活跃状态 0 活跃 1 不活跃
     */
    private boolean enabled;

    /**
     * 头像
     */
    private String avatar;

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
     * 发行 0 发行 1 未发行
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

    /**
     * 权限相关uuid
     */
    private String objectPk;

    /**
     * 创建人
     */
    private String user;

    private static final long serialVersionUID = 1L;
}