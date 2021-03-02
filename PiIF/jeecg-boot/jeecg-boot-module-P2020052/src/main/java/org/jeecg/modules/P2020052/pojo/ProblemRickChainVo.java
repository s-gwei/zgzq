package org.jeecg.modules.P2020052.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ProblemRickChainVo implements Serializable {

    private String id;

    private String risk_name;//风险名称

    private String confirm_status;//风险状态

    private String risk_to_type;//风险类型

    private String risk_code;//风险编码

    private String risk_description;

    private List<ProblemRickChainVo> children ;


}
