package org.jeecg.modules.investigation.vo;

import lombok.Data;
import org.jeecg.modules.investigation.model.TemplateDetail;

import java.io.Serializable;
import java.util.List;

/**
 * @author xduan
 * @version 2020/4/29
 */
@Data
public class ReportScoreVO implements Serializable {
    /**
     * 评分类型的id
     */
    private String Id;

    /**
     * 评价类型
     */
    private String type;

    /**
     * 每项评分详情
     */
    private List<TemplateDetail> details;

    /**
     * 参与评价的人员
     */
    private String evaluators;

    private static final long serialVersionUID = -1955124165252295484L;
}
