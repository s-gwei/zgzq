package org.jeecg.modules.P2020052.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.P2020052.service.ReportService;
import org.jeecg.modules.P2020052.service.ZLService;
import org.jeecg.modules.P2020052.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/ZLTable")
@Slf4j
@Api(tags="ZL的报表")
public class ZLController {

    @Autowired
    ZLService ZLService;

    @GetMapping(value = "/pertTable")
    @ApiOperation(value = "pert表")
    public Result pert(String activeId) throws ParseException{
        return Result.ok(ZLService.pertTable(activeId));
    }
    @GetMapping(value = "ProjectRiskTable")
    @ApiOperation(value = "项目风险系数表")
    public Result ProjectRiskTable(String projectIds) throws ParseException {
        return Result.ok(ZLService.ProjectRiskTable(projectIds));
    }

}
