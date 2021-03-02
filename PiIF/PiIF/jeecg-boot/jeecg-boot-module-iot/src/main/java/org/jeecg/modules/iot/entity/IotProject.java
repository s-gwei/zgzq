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
 * @Desc 项目实体
 */
@Data
@TableName("iot_project")
public class IotProject implements Serializable {
    private static final long serialVersionUID = 5472758517749859840L;
    // id
    @TableId(type = IdType.UUID)
    private String id;
    
    private String name;
    
    private String icon;
    
    private String desciption;
    
    private String projectPk;
    
    private String tagPk;
    
    private String mashupName;
    //修改时间
    private Timestamp updateTime;
    //创建时间
    private Timestamp createTime;
    
    private String document;
    //可见性权限的关联主键
    private String objectPk;
}
