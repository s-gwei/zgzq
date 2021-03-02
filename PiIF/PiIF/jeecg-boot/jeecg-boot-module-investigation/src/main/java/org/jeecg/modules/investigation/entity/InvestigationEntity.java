package org.jeecg.modules.investigation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author xduan
 * @version 2020/4/24
 * @Desc 调查问卷的实例表，实体类
 */
@Data
@TableName("investigation_entity")
public class InvestigationEntity implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.UUID)
    private String id;
    /**
     * 实例名称
     */
    private String name;
    /**
     * 实例描述
     */
    private String description;
    /**
     * 实例的核心内容
     */
    private String template;

    /**
     * 头像
     */
    private String icon;
    /**
     * 版本信息
     */
    private Integer version;
    /**
     * 模板id
     */
    private Integer tempId;
    /**
     * 问卷的评价人的id
     */
    private String evaluatorUserId;
    /**
     * 问卷的评价人，姓名
     */
    private String evaluatorRealname;
    /**
     * 问卷的考核人 id
     */
    private String assessorUserId;

    /**
     * 问卷的考核人，姓名
     */
    private String assessorRealname;

    /**
     * 问卷的状态（发布 published，编辑 edit，完成complete，驳回reject）
     */
    private String status;

    /**
     * 评价人的权重，除了PM以外，其它的都是根据评价人的人头均摊。
     */
    private Double qWeight;

    /**
     * 活动容器id
     */
    private Integer containerId;

    /**
     * 最终得分，精确到小数点一位
     */
    private Double score;

    /**
     * 问卷模板表的问卷调查权重
     */
    private Integer weight;

    private static final long serialVersionUID = -6890368587198932528L;
}
