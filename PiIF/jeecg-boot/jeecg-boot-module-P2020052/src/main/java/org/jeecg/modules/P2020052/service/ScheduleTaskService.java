package org.jeecg.modules.P2020052.service;

import org.springframework.stereotype.Repository;

import java.text.ParseException;

@Repository
public interface ScheduleTaskService {
    void taskExecution();

    void ProjectRiskTable() throws ParseException;
}
