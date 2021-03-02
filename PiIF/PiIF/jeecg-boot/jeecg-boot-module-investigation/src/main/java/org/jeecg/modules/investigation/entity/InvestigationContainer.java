package org.jeecg.modules.investigation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author xduan
 * @version 2020/4/24
 * @Desc 活动表实体
 */
@Data
@TableName("investigation_container")
public class InvestigationContainer implements Serializable {
    //主键，自增
    @TableId(type = IdType.AUTO)
    private Integer id;
    //活动名称
    private String name;
    //描述
    private String description;
    //图标
    private String icon;
    /**
     * 'false /0代表正常，true/1代表删除',默认正常
     */
    private Boolean delFlag = false;

    private static final long serialVersionUID = -4757194386047799772L;
}
