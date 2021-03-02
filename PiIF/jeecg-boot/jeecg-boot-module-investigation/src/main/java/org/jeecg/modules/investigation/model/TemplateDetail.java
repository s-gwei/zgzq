package org.jeecg.modules.investigation.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

/**
 * @author xduan
 * @version 2020/4/24
 * @Desc 调查问卷的模板 详情格式模型
 * <p>
 * 'id': 'DA001',
 * 'name': '定量指标',
 * 'score': '',
 * 'reason': '',
 * 'weight': '',
 * 'Weighting': '',
 * 'desc': ''
 */
@Data
public class TemplateDetail implements Serializable {
    private static final long serialVersionUID = 2763434209016508461L;
    /**
     * id
     */
    private String id;
    /**
     * 评价项名称
     */
    private String name;
    /**
     * 评价分数
     */
    private String score;
    /**
     * 评价理由
     */
    private String reason;
    /**
     * 权重,当前评价项的占当前问卷的权重比例
     */
    private String weight;
    /**
     * 加权重，当前评价项的总 问卷的权重比例
     */
    private String weighting;
    /**
     * 描述
     */
    private String desc;

    /**
     * 评分详情
     */
    @JSONField(serialize = false)
    private String detail;
}
