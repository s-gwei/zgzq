package org.jeecg.modules.P2020052.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.P2020052.pojo.ProjectLevelVo;
import org.jeecg.modules.P2020052.pojo.ProjectTypeVo;
import org.jeecg.modules.P2020052.pojo.RiskMeasureVo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface HomeTableMapper {

    List<ProjectTypeVo> projectType();

    List<ProjectLevelVo> projectLevel();

    Map<String, Object> riskPrevention();

    RiskMeasureVo selectRiskTable(@Param("start") String startStr, @Param("end") String endStr);
}
