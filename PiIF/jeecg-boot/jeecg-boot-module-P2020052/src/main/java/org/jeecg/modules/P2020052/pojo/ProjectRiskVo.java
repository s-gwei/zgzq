package org.jeecg.modules.P2020052.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProjectRiskVo implements Serializable {

    private  String  projectId;//项目id

    private String name;//项目名称

    private String preRatio;//项目预实比

    private String startTime;//项目启动时间和当前日期差

    private String standardDeviationValue;//标准偏差

    private String standardDifficultyValue;//标准困难度

    private String deviationReport;//汇报偏差

    private String difficultyReport;//汇报困难度

}
