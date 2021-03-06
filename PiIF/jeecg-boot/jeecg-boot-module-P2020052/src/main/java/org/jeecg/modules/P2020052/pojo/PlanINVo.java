package org.jeecg.modules.P2020052.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PlanINVo implements Serializable {

     private String plan_activity_ref_id;//(任务id)

     private String code;//(编码)
     private String codeDescription;//编码描述

     private String name;//任务名称

     private String weights;//权重

     private String is_deleted;//是否删除

     private String is_persisted;//是否存在

     private String update_count;//更改次数

     private String description;//描述

     private Double ot_rating;//评定

     private String  reportTime;//汇报时间



}
