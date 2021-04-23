package org.jeecg.modules.P2020052.service;

import org.jeecg.modules.P2020052.pojo.PiplanActivityVo;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.util.List;

@Repository
public interface ZLService {
    List<PiplanActivityVo> pertTable(String activeId) throws ParseException;
    List ProjectRiskTable(String projectIds) throws ParseException;
    List SectorRiskFactor(String userIds, String name, String startTime, String endTime) throws ParseException;
}
