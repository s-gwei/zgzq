package org.jeecg.modules.P2020052.service;

import org.jeecg.modules.P2020052.pojo.PiplanActivityVo;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Repository
public interface ReportService {
    List<PiplanActivityVo> pertTable(String activeId) throws ParseException;

    List SectorRiskFactor(String userIds, String name, String startTime, String endTime) throws ParseException;

    List ProjectRiskTable(String projectIds) throws ParseException;

    List groupUser(String projectId);

    List taskExecution(String projectId, String userIds);

    void exportTaskTable(HttpServletResponse response, String fileName, String projectId, String userIds) throws IOException;
}
