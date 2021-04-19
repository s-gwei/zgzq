package org.jeecg.modules.P2020052.service;

import org.jeecg.modules.P2020052.pojo.PiplanActivityVo;
import org.jeecg.modules.P2020052.pojo.ProjectLevelVo;
import org.jeecg.modules.P2020052.pojo.ProjectTypeVo;
import org.jeecg.modules.P2020052.pojo.RiskMeasureVo;
import org.jeecg.modules.P2020052.util.Result;

import java.util.List;
import java.util.Map;

public interface HomeTableService {
    Map<String,Object> projectType();

    List<ProjectLevelVo> projectLevel();

    List<Map<String, Object>> riskPrevention();

    List<Map<String, Object>> selectRiskTable();
}
