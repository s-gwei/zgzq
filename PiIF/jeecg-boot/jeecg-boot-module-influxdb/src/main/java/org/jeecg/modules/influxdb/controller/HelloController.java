package org.jeecg.modules.influxdb.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.influxdb.config.InfluxDbUtils;
import org.jeecg.modules.influxdb.entity.Computer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试新的module
 * @author chengtg
 *
 */
@Slf4j
@Api(tags="InfluxDBDemo")
@RestController
@RequestMapping("/influxdb")
public class HelloController {

    @Autowired
    private InfluxDB influxDB;

    @ApiOperation("Influxdb_Computer")
    @GetMapping(value = "/install/computer")
    public Result<String> hello(Computer computer) {

        Point point = Point.measurementByPOJO(computer.getClass()).addFieldsFromPOJO(computer).build();

        influxDB.write("pisx", InfluxDbUtils.POLICY_NAME_PIX, point);

        Result<String> result = new Result<String>();
        result.setResult(computer.toString());
        result.setSuccess(true);

        return result;

    }


}
