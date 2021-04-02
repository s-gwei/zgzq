package org.jeecg.modules.P2020052.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class GroupRiskVo implements Serializable {

    private  String activityId;//任务id

    private  String actualWorkQty;//实际工时

    private  String standardWorkQty;//计划工时

    private String actualStartDate;//计划开始时间

    private String weights;//权重

    private String standardDeviationValue;//标准偏差

    private String standardDifficultyValue;//标准困难度

    private String deviationReport;//汇报偏差

    private String difficultyReport;//汇报困难度

    private String breadth;//广度

    private String criticality;//关键度




}
