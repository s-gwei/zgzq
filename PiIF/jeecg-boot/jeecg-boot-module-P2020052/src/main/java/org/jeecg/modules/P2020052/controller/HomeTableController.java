package org.jeecg.modules.P2020052.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.P2020052.pojo.*;
import org.jeecg.modules.P2020052.service.HomeTableService;
import org.jeecg.modules.P2020052.service.PlanOTService;
import org.jeecg.modules.P2020052.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author sungw
 * 2021/4/14
 *
 */
@RestController
@RequestMapping("/HomeTable")
@Slf4j
@Api(tags="首页报表展示")
public class HomeTableController {

    @Autowired
    HomeTableService homeTableService;


    @Autowired
    PlanOTService planOTService;

    /**
     * 项目类别展示
     */
    @GetMapping(value = "/projectType")
    @ApiOperation(value = "项目类别展示报表")
    public Result projectType(){
        Map<String,Object> map =homeTableService.projectType();
        return Result.ok(map);
    }
    /**
     * 项目级别
     */
    @GetMapping(value = "/projectLevel")
    @ApiOperation(value = "项目级别展示报表")
    public Result projectLevel(){
        List<ProjectTypeVo> list =homeTableService.projectLevel();
        return Result.ok(list);
    }
    /**
     * 风险预防措施认可数和未认可数
     *
     */
    @GetMapping(value = "/riskPrevention")
    @ApiOperation(value = "风险预防措施认可数和未认可数")
    public Result riskPrevention(){
        List<Map<String,Object>> list = homeTableService.riskPrevention();
        return Result.ok(list);
    }

    /**
     * 大屏展示工作任务延期
     *
     */
    @GetMapping(value = "/WorkDelayTable")
    @ApiOperation(value = "工作任务延期报表")
    public Result WorkDelayTable() throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        String end = df.format(new Date());
        //过去一月
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, -2);
        Date m = c.getTime();
        String start = df.format(m);
        String[] time = {start,end};
        List<PiplanActivityVo> list = planOTService.WorkDelayTable(time,null,null,null);
        return Result.ok(list);
    }

    /**
     * 项目风险预防措施
     *
     */
    @GetMapping(value = "/selectRiskTable")
    @ApiOperation(value = "项目风险及预防措施")
    public Result selectRiskTable() throws ParseException {
        List<Map<String, Object>> list = homeTableService.selectRiskTable();
        return Result.ok(list);
    }

    /**
     * 项目风险详情
     *
     */
    @GetMapping(value = "/selectRisk")
    @ApiOperation(value = "项目风险详情")
    public Result selectRisk(String startTime, String endTime)   {
        List<RiskVo> list = homeTableService.selectRisk(startTime,endTime);
        return Result.ok(list);
    }

    /**
     * 项目预防措施详情
     *
     */
    @GetMapping(value = "/selectMeaTable")
    @ApiOperation(value = "项目预防措施详情")
    public Result selectMeaTable(String startTime, String endTime)  {
        List<RiskVo> list = homeTableService.selectMeaTable(startTime,endTime);
        return Result.ok(list);
    }

    /**
     * 项目预防措施认可数未认可数详情
     *
     */
    @GetMapping(value = "/riskPreventionetails")
    @ApiOperation(value = "项目预防措施认可数未认可数详情")
    public Result riskPreventionetails(int state)  {
        List<RiskVo> list = homeTableService.riskPreventionetails(state);
        return Result.ok(list);
    }
}
