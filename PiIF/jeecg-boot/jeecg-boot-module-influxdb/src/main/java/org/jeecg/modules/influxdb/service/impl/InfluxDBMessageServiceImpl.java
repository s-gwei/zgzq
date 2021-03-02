package org.jeecg.modules.influxdb.service.impl;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.influxdb.config.InfluxDbUtils;
import org.jeecg.modules.influxdb.service.IInfluxDBMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @version:2020/3/4
 * @Author:wzp
 * @Date:Created in 15:23 2020/3/4
 * @Description:接收消息
 */
@Service
public class InfluxDBMessageServiceImpl implements IInfluxDBMessageService {

    @Autowired
    private InfluxDbUtils influxDbUtils;

    /**
     * 查询influxdb表中所有的列
     *
     * @param tableName 表名
     * @return Result
     */
    @Override
    public Result<Object> findColumnsByTableName(String tableName) {
        String dbTableName = "iotgateway" + "/" + tableName;
        Result<Object> objectResult = new Result<>();
        InfluxDB influxDB = influxDbUtils.getInfluxDB();
        QueryResult qr = influxDB.query(new Query("select * from " + "\"" + dbTableName + "\""));
        for (QueryResult.Result result : qr.getResults()) {
            List<QueryResult.Series> series = result.getSeries();
            for (QueryResult.Series series1 : series) {
                List<String> columns = series1.getColumns();
                objectResult.setResult(columns);
            }
        }
        return objectResult;
    }
}


