package org.jeecg.modules.investigation.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author xduan
 * @version 2020/4/29
 * 报表大模型
 */
@Data
public class ReportVO implements Serializable {
    /**
     * 被评价人姓名
     */
    private String realName;

    private Double score;

    private String status;

    private List<ReportScoreVO> list;


    private static final long serialVersionUID = 6506198894392936109L;
}
