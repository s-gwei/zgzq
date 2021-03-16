package org.jeecg.modules.P2020052.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.P2020052.pojo.PlanINVo;
import org.jeecg.modules.P2020052.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/HR_INTable")
@Slf4j
@Api(tags="HR的IN报表")
public class HR_PlanINController {
    @Autowired
    org.jeecg.modules.P2020052.service.HR_INService HR_INService;

    @GetMapping(value = "/selectINTable")
    @ApiOperation(value = "查询in数据")
    public Result selectINTable(
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            String[] time,String[] group,String planId) {
        Result<IPage<PlanINVo>> result = new Result<IPage<PlanINVo>>();
        Page page = new Page(pageNo, pageSize);
        IPage<PlanINVo> list = HR_INService.selectINTable(page, time, group,planId);
        return result.ok(list);
    }
}
