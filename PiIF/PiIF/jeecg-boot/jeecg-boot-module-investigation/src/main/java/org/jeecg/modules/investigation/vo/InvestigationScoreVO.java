package org.jeecg.modules.investigation.vo;

import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * @author xduan
 * @version 2020/4/27
 * 问卷得分实体
 */
@Data
public class InvestigationScoreVO implements Serializable {
    /**
     * 员工id
     */
    @Excel(name = "员工id")
    private String userId;
    /**
     * 员工名称
     */
    @Excel(name = "员工名称")
    private String realname;

    /**
     * 总分
     */
    @Excel(name = "总分")
    private Double score;
    /**
     * 评分完成，评分未完成
     */
    @Excel(name = "状态")
    private String status;

    private static final long serialVersionUID = 1724074683468780264L;
}
