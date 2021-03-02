package org.jeecg.modules.influxdb.service;

import org.jeecg.common.api.vo.Result;

/**
 * @version:2020/3/4
 * @Author:wzp
 * @Date:Created in 15:23 2020/3/4
 * @Description:表信息接口
 */
public interface IInfluxDBMessageService {


    /**
     * 查询influxdb表中所有的列
     *
     * @param tableName 表名
     * @return Result
     */
    Result<Object> findColumnsByTableName(String tableName);

}
