package org.jeecg.modules.iot.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 物模板关联部门，控制可见性
 *
 * @author 张泽革
 * @create 2020/3/5
 */
@Data
@TableName("thingtemplate_depart")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "thingtemplate_depart对象", description = "物模板关联部门，控制可见性")
public class ThingtemplateDepartVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 物模板id
     */
    private String thingtemplateId;
    /**
     * 部门id
     */
    private String departId;

    public ThingtemplateDepartVO() {
        super();
    }

    public ThingtemplateDepartVO(String thingtemplateId, String departId) {
        super();
        this.departId = departId;
        this.thingtemplateId = thingtemplateId;
    }
}
