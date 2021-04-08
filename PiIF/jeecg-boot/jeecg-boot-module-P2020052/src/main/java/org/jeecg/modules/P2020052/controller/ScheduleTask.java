package org.jeecg.modules.P2020052.controller;

import org.jeecg.modules.P2020052.service.PlanOTService;
import org.jeecg.modules.P2020052.service.ScheduleTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
public class ScheduleTask  {


    @Autowired
    ScheduleTaskService scheduleTaskService;
//    @Scheduled(fixedRate = 1000)
    //从上一个任务开始到下一个任务开始的间隔，单位是毫秒。
    public void job1(){
        System.out.println("任务进行中。。。。fixedRate");
    }

    //获取任务执行报表
    @Scheduled(cron ="${cron}")
    public void taskExecution(){
        scheduleTaskService.taskExecution();
        System.out.println("任务执行。。。。cron");
    }


    @Scheduled(cron ="${cron}")
    public void ProjectRiskTable() throws ParseException {
        scheduleTaskService.ProjectRiskTable();
        System.out.println("任务执行。。。。cron");
    }

}
