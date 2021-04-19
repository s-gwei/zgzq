package org.jeecg.modules.P2020052.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProjectTypeVo implements Serializable {

    private String id;

    //总数
    private Double count;
    //名称
    private String name;
    //百分比
    private Double percentage;


}
