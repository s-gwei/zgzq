package org.jeecg.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 物模板关联部门，控制可见性
 * @author 张泽革
 * @create 2020/3/5
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ThingtemplateDepart implements Serializable {
    private static final long serialVersionUID = 1L;
    /**id*/
    @TableId(type = IdType.ID_WORKER_STR)
    private String id;
    /**物模板id*/
    private String thingtemplateId;
    /**部门id*/
    private String departId;

    public ThingtemplateDepart(String thingtemplateId, String departId) {
        this.thingtemplateId = thingtemplateId;
        this.departId = departId;
    }
}
