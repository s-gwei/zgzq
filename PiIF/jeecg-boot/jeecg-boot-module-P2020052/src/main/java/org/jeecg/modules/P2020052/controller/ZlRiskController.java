package org.jeecg.modules.P2020052.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.P2020052.service.ZLService;
import org.jeecg.modules.P2020052.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/ZLTable")
@Slf4j
@Api(tags="ZL部门风险系数报表")
public class ZlRiskController {

    @Autowired
    ZLService ZLService;

    @GetMapping(value = "SectorRiskFactorTable")
    @ApiOperation(value = "部门风险系数表")
    public Result SectorRiskFactor(String userIds, String name, String startTime, String endTime) throws ParseException {
        return Result.ok(ZLService.SectorRiskFactor(userIds,name,startTime,endTime));
    }

}
