package org.jeecg.modules.workflow.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author xduan
 * @version 2020/6/29
 * @Desc 工作流节点配置对象
 */
@Data
@TableName("workflow_node_config")
public class WorkflowNodeConfig implements Serializable {
    private static final long serialVersionUID = 9212255583550409207L;
    /**
     * 主键
     */
    private String id;

    /**
     * 流程节点名称
     */
    private String processNodeName;

    /**
     *流程节点编码
     */
    private String processNodeCode;

    /**
     * 流程id
     */
    private String processId;

    /**
     * 流程节点状态
     */
    private String nodeBpmStatus;

    /**
     * PC表单地址
     */
    private String modelAndView;

    /**
     * 移动表单地址
     */
    private String modelAndViewMobile;

    /**
     * 表单id
     */
    private String formId;

    /**
     * 超时时间 /小时
     */
    private Integer nodeTimeout;
}
