package org.jeecg.modules.P2020052.pojo;

import lombok.Data;

import java.util.Date;
@Data
public class RiskVo {

    private String riskName;//风险名称

    private String id;//措施id

    private Date createStamp;//风险提出时间

    private String riskDescription;//风险描述

    private String userName;//风险提出人

    private String name;//措施名称

    private String precaution;//预防措施描述

    private String groupName;//涉及部门

    private String user_name;//责任人




}
