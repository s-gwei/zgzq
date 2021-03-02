package org.jeecg.modules.workflow.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author xduan
 * @version 2020/6/29
 */
@Data
@TableName("workflow_form")
public class WorkflowForm  implements Serializable {
    
    private String id;
    
    /**
     * 流程状态列
     */
    private String flowStatusCol;

    /**
     * 表单默认风格
     */
    private String formDealStyle;

    /**
     * 表单名称
     */
    private String formTableName;

    /**
     * 表单类型
     */
    private String formType;

    /**
     * 流程id
     */
    private String processId;

    /**
     * 关系代码
     */
    private String relationCode;

    /**
     * 标题表达式
     */
    private String titleExp;
    
    private static final long serialVersionUID = -8472920199068302702L;
}
