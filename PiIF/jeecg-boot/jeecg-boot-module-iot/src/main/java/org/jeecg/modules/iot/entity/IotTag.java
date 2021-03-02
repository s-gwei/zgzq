package org.jeecg.modules.iot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author xduan
 * @version 2020/4/9
 * @Desc 标签实体
 */
@Data
@TableName("iot_tag")
public class IotTag implements Serializable {
    private static final long serialVersionUID = -5384142250542397107L;
    //id
    @TableId(type = IdType.UUID)
    private String id;
    
    private String name;
    
    private String icon;
    
    private String desciption;
    //关联项目
    private String projectPk;
    //关联标签的
    private String tagPk;

    private String mashupName;
    //修改时间（数据库在插入或修改时自动更新）
    private Timestamp updateTime;
    //创建时间
    private Timestamp createTime;
    
    private String document;
    //可见性权限的关联主键
    private String objectPk;
}
