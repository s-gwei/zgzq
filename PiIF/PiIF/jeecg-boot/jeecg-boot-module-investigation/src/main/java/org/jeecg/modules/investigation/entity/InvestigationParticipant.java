package org.jeecg.modules.investigation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author xduan
 * @version 2020/4/24
 * @Desc 记录调查活动的参与人及状态
 */
@Data
@TableName("investigation_participant")
public class InvestigationParticipant implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 活动表(investigation_container)的id
     */
    private Integer containerId;
    /**
     * 被评价人 id
     */
    private String userId;

    /**
     * 调查问卷分发状态：未发布 ，已发布
     */
    private String status;

    private static final long serialVersionUID = 2495902169556181920L;
}
