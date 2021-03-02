package org.jeecg.modules.influxdb.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.StringHandleUtil;
import org.jeecg.modules.influxdb.service.IInfluxDBMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * @version:2020/3/4
 * @Author:wzp
 * @Date:Created in 15:23 2020/3/4
 * @Description:接收消息
 */
@RestController
@RequestMapping("/influxDBMessage")
@Slf4j
@Api("influxdb")
public class InfluxDBMessageController {

    @Autowired
    private IInfluxDBMessageService influxDBMessageService;

    /**
     * 查询influxdb表中所有的列
     *
     * @param tableName 表名
     * @return Result
     */
    @RequestMapping(value = "/findColumnsByTableName", method = RequestMethod.GET)
    @ApiOperation("查询influxdb表中所有的列")
    public Result<Object> findColumnsByTableName(String tableName) {
        Result<Object> result = new Result<>();
        try {
            //非空效验
            StringHandleUtil.validateParam("数据异常,请联系管理员", tableName);
            result = influxDBMessageService.findColumnsByTableName(tableName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }


}
