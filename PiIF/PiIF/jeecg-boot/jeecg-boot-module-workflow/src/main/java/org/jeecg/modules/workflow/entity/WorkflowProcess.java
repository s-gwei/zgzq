package org.jeecg.modules.workflow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author xduan
 * @version 2020/6/19
 * 工作流设计的实体类
 */
@Data
@TableName("workflow_process")
public class WorkflowProcess implements Serializable {
    /**
     * 主键id,UUID
     */
    @TableId(type = IdType.UUID)
    private String id;

    /**
     * 流程名称 唯一
     */
    private String processName;

    /**
     * 流程定义key 唯一
     */
    private String processKey;

    /**
     * 流程类型
     */
    private String processType;

    /**
     * 流程定义文件的内容
     */
    private String processXml;

    /**
     * 流程定义文件的上传地址
     */
    private String processXmlPath;

    /**
     * 流程发布状态 ：0/未发布/false，1/已发布/true
     */
    private Boolean processStatus;

    /**
     * 创建人
     */
    private String createBy;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String updateBy;

    /**
     * 修改时间
     */
    private Date updateTime;
    
    /**
     *pc 图标 
     */
    private String pcIcon;

    /**
     * app 图标
     */
    private String appIcon;

    /**
     *
     */
    private String note;

    private static final long serialVersionUID = -4747658626781001029L;
}
