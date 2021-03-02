package org.jeecg.modules.investigation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author xduan
 * @version 2020/4/24
 * @Desc 关系评价维护表
 */
@Data
@TableName("investigation_relationship")
public class InvestigationRelationship implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 描述
     */
    private String description;
    /**
     * 头像
     */
    private String icon;
    /**
     * 评价人的权重 保留2位小数
     */
    private Double qWeight;
    /**
     * 评价人 的id
     */
    private String evaluatorUserId;
    /**
     * 评价人真实姓名
     */
    private String evaluatorRealname;
    /**
     * 评价人的用户名
     */
    private String evaluatorUsername;
    /**
     * 关系类型:个人 individual，同行 peer ，项目经理 pm ，总监 director
     */
    private String relationship;
    /**
     * 被评价人的id
     */
    private String assessorUserId;

    /**
     * 被评价人真实姓名
     */
    private String assessorRealname;

    private static final long serialVersionUID = 2973679408062812222L;
}
