package org.jeecg.modules.iot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 物实例关联部门，控制可见性
 *
 * @author 张泽革
 * @create 2020/3/6
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("iot_thing_depart")
public class ThingDepart implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(type = IdType.ID_WORKER_STR)
    private String id;
    /**
     * 物模板id
     */
    private String thingId;
    /**
     * 部门id
     */
    private String departId;
    /**
     * 是否是继承的权限
     */
    private Boolean isExtend;


    public ThingDepart(String thingId, String departId) {
        this.thingId = thingId;
        this.departId = departId;
    }


}
