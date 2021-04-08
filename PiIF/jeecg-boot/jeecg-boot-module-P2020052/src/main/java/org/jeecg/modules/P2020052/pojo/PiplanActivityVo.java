package org.jeecg.modules.P2020052.pojo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

@Data
public class PiplanActivityVo implements Serializable {

    private String  activityId;//任务ID

    private String  activityName;//任务名称

    private String  projectName;//项目名称

    private String  taskType;//任务状态

    private Double deviation;//偏差值

    private Double Xaxis;//横坐标

    private String targetStartTime;//预估开始时间

    private String byTime;//截至时间（计划完成时间）

    private String expectedFinishTime;//预估完成时间

    private String actualStartTime;//实际开始时间

    private String actualEndTime;//实际完成时间
}
