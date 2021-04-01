package org.jeecg.modules.P2020052.pojo;

import lombok.Data;

import java.io.Serializable;
@Data
public class PlanOTVo implements Serializable {

     private String plan_activity_ref_id;//(任务id)

     private String code;//(编码)

     private String name;//任务名称

     private  String description;//描述

     private  String  reportTime;//汇报时间

     private String deviation_report;//(汇报偏差)

     private String difficulty_report;//(汇报困难度)

     private String breadth;//(广度)

     private String criticiailty;//（关键度）

     private String standard_deviation_value;//(标准偏差)

     private String standard_difficulty_value;//(标准困难度)
}
