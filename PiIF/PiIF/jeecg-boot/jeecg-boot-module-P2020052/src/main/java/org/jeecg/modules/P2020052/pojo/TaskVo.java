package org.jeecg.modules.P2020052.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class TaskVo implements Serializable {

    private String executive;//执行职能

    private String planName;//任务名称

    private String planId;//任务id

    private String weights;//权重

    private String standardDeviationValue;//标准偏差

    private String standardDifficultyValue;//标准困难度

    private String deviationReport;//汇报偏差

    private String difficultyReport;//汇报困难度

    private String breadth;//广度

    private String criticality;//关键度

    private String standardWork;//标准工期

    private String reportWork;//项目工期

    private String Output ;//输出评定

    private String releases;//发布次数



}
