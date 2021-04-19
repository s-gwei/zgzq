package org.jeecg.modules.P2020052.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class RiskMeasureVo implements Serializable {

    private Integer weeks;//周数

    private Integer risk;//风险数

    private Integer measures;//预防措施数



}
