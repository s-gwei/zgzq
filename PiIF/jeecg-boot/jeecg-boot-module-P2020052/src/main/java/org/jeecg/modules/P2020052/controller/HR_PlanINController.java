package org.jeecg.modules.P2020052.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.P2020052.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/HRTable")
@Slf4j
@Api(tags="HR的IN报表")
public class HR_PlanINController {
    @Autowired
    org.jeecg.modules.P2020052.service.HR_INService HR_INService;

    @GetMapping(value = "/HR_INTable")
    @ApiOperation(value = "HR_IN表")
    public Result HR_IN(String activeId) throws ParseException {
        return Result.ok(HR_INService.HR_INTable(activeId));
    }
}
