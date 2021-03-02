package org.jeecg.modules.investigation.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xduan
 * @version 2020/4/26
 * @desc 关系模型，用于批量修改权重
 */
@Data
public class RelationshipVO implements Serializable {
    private static final long serialVersionUID = 6364402666646116159L;
    /**
     * 关系id
     */
    private Integer id;
    /**
     * 权重
     */
    private Double qWeight;
}
