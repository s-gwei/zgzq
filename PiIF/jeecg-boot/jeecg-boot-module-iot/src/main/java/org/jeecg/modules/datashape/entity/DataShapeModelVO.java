package org.jeecg.modules.datashape.entity;

import lombok.Data;

/**
 * @Author:wzp
 * @Date:Created in 10:55 AM 2020/4/9
 * @Description:接收前端发送的datashape实例
 */
@Data
public class DataShapeModelVO {

    /**
     * id
     */
    private int id;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 表名
     */
    private String name;

    /**
     * 表描述
     */
    private String description;

    /**
     * 使用说明
     */
    private String documentationcontent;

    /**
     * 主页
     */
    private String homemashup;

    /**
     * 项目
     */
    private String projectname;

    /**
     * 标签
     */
    private String tags;

    /**
     * 创建人
     */
    private String user;

}
