package org.jeecg.modules.investigation.vo;

import lombok.Data;
import org.jeecg.modules.investigation.entity.InvestigationEntity;

import java.io.Serializable;
import java.util.List;

/**
 * @author xduan
 * @version 2020/4/28
 * @desc 个人分数详情页
 */
@Data
public class ScoreDetailVO implements Serializable {
    /**
     * 员工id
     */
    private String userId;
    /**
     * 员工名称
     */
    private String realname;
    /**
     * 总分
     */
    private Double score;

    /**
     * 评分完成，评分未完成
     */
    private String status;
    /**
     * 所有评分问卷详情
     */
    private List<List<InvestigationEntity>> details;
    private static final long serialVersionUID = 3165231010972545675L;
}
