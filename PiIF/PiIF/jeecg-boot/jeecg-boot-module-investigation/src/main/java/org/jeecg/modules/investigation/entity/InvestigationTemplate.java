package org.jeecg.modules.investigation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author xduan
 * @version 2020/4/24
 * @Desc 调查问卷模板表实体类
 */
@Data
@TableName("investigation_template")
public class InvestigationTemplate implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 名称
     */
    private String name;

    /**
     * 模板描述
     */
    private String description;

    /**
     * template,模板的核心内容，json
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
     * 调查问卷的权重
     */
    private Integer weight;
    /**
     * 关系类型
     */
    private String relationship;

    private static final long serialVersionUID = 2743002831846049366L;
}
