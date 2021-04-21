package org.jeecg.modules.P2020052.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.P2020052.pojo.ProjectLevelVo;
import org.jeecg.modules.P2020052.pojo.ProjectTypeVo;
import org.jeecg.modules.P2020052.pojo.RiskMeasureVo;
import org.jeecg.modules.P2020052.pojo.RiskVo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface HomeTableMapper {

    List<ProjectTypeVo> projectType();

    Map<String,Object> projectLevel();

    Map<String, Object> riskPrevention();

    RiskMeasureVo selectRiskTable(@Param("start") String startStr, @Param("end") String endStr);

    List<RiskVo> selectRisk(@Param("startTime")String startTime, @Param("endTime")String endTime);

    List<RiskVo> selectMeaTable(@Param("startTime")String startTime, @Param("endTime")String endTime);

    List<RiskVo> riskPreventionetails(@Param("state")int state);
}
