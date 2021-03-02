package org.jeecg.modules.P2020052.pojo;

import lombok.Data;

import java.util.Date;
@Data
public class RiskVo {

    private String risk_name;//风险名称

    private String id;//措施id

    private Date create_time;//创建时间

    private String name;//措施名称

    private String is_deleted;//是否删除

    private String is_persisted;//是否存在

    private String update_count;//更改次数


}
