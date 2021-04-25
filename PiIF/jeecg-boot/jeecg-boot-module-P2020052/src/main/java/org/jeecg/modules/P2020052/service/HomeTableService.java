package org.jeecg.modules.P2020052.service;

import org.jeecg.modules.P2020052.pojo.*;
import org.jeecg.modules.P2020052.util.Result;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface HomeTableService {
    Map<String,Object> projectType();

    List<ProjectTypeVo> projectLevel();

    List<Map<String, Object>> riskPrevention();

    List<Map<String, Object>> selectRiskTable() throws ParseException;


    List<RiskVo> selectRisk(String startTime, String endTime);

    List<RiskVo> selectMeaTable(String startTime, String endTime);

    List<RiskVo> riskPreventionetails(int state);

    List<PiplanActivityVo> WorkDelayTable();
}
