package org.jeecg.modules.P2020052.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.P2020052.pojo.PiplanActivityVo;
import org.jeecg.modules.P2020052.service.ReportService;
import org.jeecg.modules.P2020052.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/HeavyDutyTable")
@Slf4j
@Api(tags="新重汽报表")
public class ReportController {

    @Autowired
    ReportService reportService;


    @GetMapping(value = "/pertTable")
    @ApiOperation(value = "pert表")
    public Result pert(String activeId) throws ParseException{
        return Result.ok(reportService.pertTable(activeId));
    }

    @GetMapping(value = "SectorRiskFactorTable")
    @ApiOperation(value = "部门风险系数表")
    public Result SectorRiskFactor(String userIds, String name, String startTime, String endTime) throws ParseException {
        return Result.ok(reportService.SectorRiskFactor(userIds,name,startTime,endTime));
    }

    @GetMapping(value = "ProjectRiskTable")
    @ApiOperation(value = "项目风险系数表")
    public Result ProjectRiskTable(String projectIds) throws ParseException {
        return Result.ok(reportService.ProjectRiskTable(projectIds));
    }

    @GetMapping(value = "TaskExecution")
    @ApiOperation(value = "任务执行报表")
    public Result taskExecution(String projectId,String userIds) throws ParseException {
        log.info("请求完毕");
        return Result.ok(reportService.taskExecution(projectId,userIds));

    }

    @GetMapping(value = "GroupUser")
    @ApiOperation(value = "部门用户关系")
    public Result groupUser(String projectId) throws ParseException {
        return Result.ok(reportService.groupUser(projectId));
    }
}
