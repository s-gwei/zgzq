package org.jeecg.modules.workflow.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author xduan
 * @version 2020/7/2
 * 流程管理相关数据对象
 */
@Data
public class ProcessInstanceVO implements Serializable {

    /**
     * 业务标题
     */
    private String title;
    /**
     * 流程定义的id
     */
    private String processDefId;

    /**
     * 流程定义的 名称
     */
    private String processDefinitionName;

    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 发起人
     */
    private String startUsername;

    /**
     * 受理人
     */
    private String assigneeName;

    /**
     * 是否挂起 ，true /挂起 ，false /激活
     */
    private Boolean isSuspended;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 当前任务id
     */
    private String taskId;

    /**
     * 当前节点名称
     */
    private String taskName;

    /**
     * 耗时
     */
    private String spendTimes;

    /**
     * 父流程id
     */
    private String parentId;

    /**
     * 子流程
     */
    private String children;

    /**
     * 状态
     */
    private String status;

    private static final long serialVersionUID = 7474752107163764540L;
}
