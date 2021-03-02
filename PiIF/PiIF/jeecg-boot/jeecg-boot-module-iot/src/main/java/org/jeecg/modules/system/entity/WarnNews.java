package org.jeecg.modules.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author zhang ho jian
 * @date 2020/9/8
 * @time 14:35
 * @Description :
 */
@Data
@TableName("iot_warn_news")
public class WarnNews {

    public String id;
    //消息名称
    public String name;
    //描述
    public String desciption;
    //触发时间
    public Date createTime;
    //确认时间
    public Date confirmTime;
    //状态
    public String state;
    //事件名称
    public String incidentName;
    //属性名称
    public String propertyName;
    //设备名称
    public String thingName;

}
