package org.jeecg.modules.P2020052.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.P2020052.pojo.*;
import org.jeecg.modules.P2020052.service.PlanOTService;
import org.jeecg.modules.P2020052.service.ScheduleTaskService;
import org.jeecg.modules.P2020052.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/OTDrivice")
@Slf4j
@Api(tags="计划OT交付情况报表")
public class PlanOTController {

    @Autowired
    PlanOTService planOTService;

    @Autowired
    ScheduleTaskService scheduleTaskService;


    @GetMapping(value = "/OTTable")
    @ApiOperation(value = "计划OT交付情况报表")
    public Result OTTable(
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            String[] time,String[] group,String planId){
        Result<IPage<PlanOTVo>> result = new Result<IPage<PlanOTVo>>();
        Page page = new Page(pageNo, pageSize);
        IPage<PlanOTVo> list = planOTService.OTTable(page,time,group,planId);
        return result.ok(list);
    }


    @GetMapping(value = "exportOTExcel")
    @ApiOperation(value = "计划OT交付情况报表导出")
    public void exportOTExcel(HttpServletResponse response,  String[] time,String[] group,String planId) throws IOException {
        planOTService.exportOTExcel(response, time,group,planId);
    }
    @GetMapping(value = "/selectGroup")
    @ApiOperation(value = "查询部门")
    public Result selectGroup(){
        List<GroupVo> list = planOTService.selectGroup();
        return Result.ok(list);
    }


    @GetMapping(value = "/selectINTable")
    @ApiOperation(value = "查询in数据")
    public Result selectINTable(
    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
    String[] time,String[] group,String planId) {
        Result<IPage<PlanINVo>> result = new Result<IPage<PlanINVo>>();
        Page page = new Page(pageNo, pageSize);
        IPage<PlanINVo> list = planOTService.selectINTable(page, time, group,planId);
        return result.ok(list);
    }
    @GetMapping(value = "exportINExcel")
    @ApiOperation(value = "计划in报表导出")
    public void exportINExcel(HttpServletResponse response,  String[] time,String[] group,String planId) throws IOException {
        planOTService.exportINExcel(response, time,group,planId);
    }

    @GetMapping(value = "/selectRiskTable")
    @ApiOperation(value = "查询风险措施")
    public Result selectRiskTable(
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            String[] time,String[] group,String planId,String projectId) {
        List<RiskVo> list = planOTService.selectRiskTable(time, group,planId,projectId);
        return Result.ok(list);
    }

    @GetMapping(value = "/exportRiskExcel")
    @ApiOperation(value = "风险措施报表导出")
    public void exportRiskExcel(HttpServletResponse response,  String[] time,String[] group,String planId,String projectId) throws IOException {
        planOTService.exportRiskExcel(response, time,group,planId,projectId);
    }
    @GetMapping(value = "/WorkDelayTable")
    @ApiOperation(value = "工作任务延期报表")
    public Result WorkDelayTable(
            String[] time,String[] group,String projectId) throws ParseException {
        List<PiplanActivityVo> list = planOTService.WorkDelayTable(time, group,projectId);
        return Result.ok(list);
    }

    @GetMapping(value = "/problemRickChain")
    @ApiOperation(value = "问题风险链")
    public Result problemRickChain(String riskId)  {
        List<ProblemRickChainVo> list = planOTService.problemRickChain(riskId);
        return Result.ok(list);
    }

    @GetMapping(value = "/taskExecution")
    @ApiOperation(value = "定时任务")
    public Result taskExecution()  {
        scheduleTaskService.taskExecution();
        return Result.ok();
    }

    @GetMapping(value = "/ptaskExecution")
    @ApiOperation(value = "项目风险系数定时任务")
    public Result projectRiskTable() throws ParseException {
        scheduleTaskService.ProjectRiskTable();
        return Result.ok();
    }

}
