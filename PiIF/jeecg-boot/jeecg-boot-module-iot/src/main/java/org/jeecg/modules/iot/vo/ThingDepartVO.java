package org.jeecg.modules.iot.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
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
@TableName("thing_depart")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "thing_depart对象", description = "物实例关联部门，控制可见性")
public class ThingDepartVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 物模板id
     */
    private String thingId;
    /**
     * 部门id
     */
    private String departId;


    public ThingDepartVO() {
        super();
    }

    public ThingDepartVO(String thingId, String departId) {
        super();
        this.departId = departId;
        this.thingId = thingId;
    }
}
