//package org.jeecg.modules.dingding.service.impl;
//
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import org.influxdb.InfluxDB;
//import org.influxdb.dto.Query;
//import org.influxdb.dto.QueryResult;
//import org.jeecg.common.api.vo.Result;
//import org.jeecg.common.util.DateUtils;
//import org.jeecg.modules.dingding.service.IDingDingService;
//import org.jeecg.modules.influxdb.config.InfluxDbUtils;
//import org.jeecg.modules.iot.util.PageUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//
//import java.util.*;
//
///**
// * @Author:wzp
// * @Date:Created in 10:33 PM 2020/4/27
// * @Description:DingDingServiceImpl
// */
//@Service
//public class DingDingServiceImpl implements IDingDingService {
//
//    @Autowired
//    private InfluxDbUtils influxDbUtils;
//
//    /**
//     * 获取钉钉考勤记录
//     *
//     * @param pageNo
//     * @param pageSize
//     * @param userName
//     * @param startTime
//     * @param endTime
//     * @param depart
//     * @return Result
//     */
//    @Override
//    public Result<Object> getAttendanceRecord(Integer pageNo, Integer pageSize, String userName, Long startTime, Long endTime, String depart) {
//        InfluxDB influxDB = influxDbUtils.getInfluxDB();
//        QueryResult qr = new QueryResult();
//        String start = "";
//        String end = "";
//        if (!StringUtils.isEmpty(startTime) || !StringUtils.isEmpty(endTime)) {
//            start = DateUtils.yyyyMMddTHHmmss.format(new Date(startTime));
//            end = DateUtils.yyyyMMddTHHmmss.format(new Date(endTime));
//        }
//        //没有条件查询
//        if (StringUtils.isEmpty(userName) && StringUtils.isEmpty(startTime) && StringUtils.isEmpty(endTime) && StringUtils.isEmpty(depart)) {
//            qr = influxDB.query(new Query("select * from " + "\"" + "allAttendanceRecord" + "\""));
//        }
//        //有条件查询  部门+用户模糊+时间区间
//        if (!StringUtils.isEmpty(userName) && !StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime) && !StringUtils.isEmpty(depart)) {
//            qr = influxDB.query(new Query(
//                    "select * from " + "\"" + "allAttendanceRecord" + "\"" + " where userName=~/" + userName + "/" +
//                            " and depart=~/" + depart + "/" +
//                            " and time >= '" + start + "' and time <= '" + end + "'"));
//        }
//        //有条件查询 部门+用户模糊
//        if (!StringUtils.isEmpty(userName) && StringUtils.isEmpty(startTime) && StringUtils.isEmpty(endTime) && !StringUtils.isEmpty(depart)) {
//            qr = influxDB.query(new Query(
//                    "select * from " + "\"" + "allAttendanceRecord" + "\"" + " where userName=~/" + userName + "/" +
//                            " and depart=~/" + depart + "/"));
//        }
//        //有条件查询  用户模糊+时间区间
//        if (!StringUtils.isEmpty(userName) && !StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime) && StringUtils.isEmpty(depart)) {
//            qr = influxDB.query(new Query(
//                    "select * from " + "\"" + "allAttendanceRecord" + "\"" + " where userName=~/" + userName + "/" +
//                            " and time >= '" + start + "' and time <= '" + end + "'"));
//        }
//        //有条件查询  部门模糊+时间区间
//        if (StringUtils.isEmpty(userName) && !StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime) && !StringUtils.isEmpty(depart)) {
//            qr = influxDB.query(new Query(
//                    "select * from " + "\"" + "allAttendanceRecord" + "\"" + " where" +
//                            " depart=~/" + depart + "/" +
//                            " and time >= '" + start + "' and time <= '" + end + "'"));
//        }
//        //有条件查询 时间区间
//        if (StringUtils.isEmpty(userName) && StringUtils.isEmpty(depart) && !StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
//            qr = influxDB.query(new Query(
//                    "select * from " + "\"" + "allAttendanceRecord" + "\"" + " where " +
//                            "time >= '" + start + "' and time <= '" + end + "'"));
//        }
//        //有条件查询 用户模糊
//        if (!StringUtils.isEmpty(userName) && StringUtils.isEmpty(startTime) && StringUtils.isEmpty(endTime) && StringUtils.isEmpty(depart)) {
//            qr = influxDB.query(new Query(
//                    "select * from " + "\"" + "allAttendanceRecord" + "\"" + " where userName=~/" + userName + "/"));
//        }
//        //有条件查询 部门模糊
//        if (!StringUtils.isEmpty(depart) && StringUtils.isEmpty(startTime) && StringUtils.isEmpty(endTime) && StringUtils.isEmpty(userName)) {
//            qr = influxDB.query(new Query(
//                    "select * from " + "\"" + "allAttendanceRecord" + "\"" + " where depart=~/" + depart + "/"));
//        }
//
//        List<Map<String, Object>> dataList = new ArrayList<>();
//        List<String> columns = new ArrayList<>();
//        for (QueryResult.Result result : qr.getResults()) {
//            List<QueryResult.Series> series = result.getSeries();
//            if (series == null) {
//                return Result.ok();
//            }
//            for (QueryResult.Series serie : series) {
//                List<List<Object>> values = serie.getValues();//字段字集合
//                columns = serie.getColumns();//字段名
//                // 封装查询结果
//                for (int i = 0; i < values.size(); ++i) {
//                    Map<String, Object> dataMap = new HashMap<>(columns.size());
//                    for (int j = 0; j < columns.size(); ++j) {
//                        dataMap.put(columns.get(j), values.get(i).get(j));
//                    }
//                    dataList.add(dataMap);
//                }
//            }
//        }
//
//        Result<Object> result = new Result<>();
//        IPage<Map<String, Object>> paging = PageUtil.paging(pageNo, pageSize, dataList);
//        result.setResult(paging);
//        result.setSuccess(true);
//        return result;
//    }
//
//    /**
//     * 获取每人每天2次的钉钉考勤记录
//     *
//     * @param pageNo
//     * @param pageSize
//     * @param userName
//     * @param startTime
//     * @param endTime
//     * @param depart
//     * @return Result
//     */
//    @Override
//    public Result<Object> getOnAndOffRecords(Integer pageNo, Integer pageSize, String userName, Long startTime, Long endTime, String depart) {
//        InfluxDB influxDB = influxDbUtils.getInfluxDB();
//        QueryResult qr = new QueryResult();
//        String start = "";
//        String end = "";
//        if (!StringUtils.isEmpty(startTime) || !StringUtils.isEmpty(endTime)) {
//            start = DateUtils.yyyyMMddTHHmmss.format(new Date(startTime));
//            end = DateUtils.yyyyMMddTHHmmss.format(new Date(endTime));
//        }
//        //没有条件查询
//        if (StringUtils.isEmpty(userName) && StringUtils.isEmpty(startTime) && StringUtils.isEmpty(endTime) && StringUtils.isEmpty(depart)) {
//            qr = influxDB.query(new Query("select * from " + "\"" + "allAttendanceRecord" + "\""));
//        }
//        //有条件查询  部门+用户模糊+时间区间
//        if (!StringUtils.isEmpty(userName) && !StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime) && !StringUtils.isEmpty(depart)) {
//            qr = influxDB.query(new Query(
//                    "select * from " + "\"" + "dayAttendanceRecord" + "\"" + " where userName=~/" + userName + "/" +
//                            " and depart=~/" + depart + "/" +
//                            " and time >= '" + start + "' and time <= '" + end + "'"));
//        }
//        //有条件查询 部门+用户模糊
//        if (!StringUtils.isEmpty(userName) && StringUtils.isEmpty(startTime) && StringUtils.isEmpty(endTime) && !StringUtils.isEmpty(depart)) {
//            qr = influxDB.query(new Query(
//                    "select * from " + "\"" + "dayAttendanceRecord" + "\"" + " where userName=~/" + userName + "/" +
//                            " and depart=~/" + depart + "/"));
//        }
//        //有条件查询  用户模糊+时间区间
//        if (!StringUtils.isEmpty(userName) && !StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime) && StringUtils.isEmpty(depart)) {
//            qr = influxDB.query(new Query(
//                    "select * from " + "\"" + "dayAttendanceRecord" + "\"" + " where userName=~/" + userName + "/" +
//                            " and time >= '" + start + "' and time <= '" + end + "'"));
//        }
//        //有条件查询  部门模糊+时间区间
//        if (StringUtils.isEmpty(userName) && !StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime) && !StringUtils.isEmpty(depart)) {
//            qr = influxDB.query(new Query(
//                    "select * from " + "\"" + "dayAttendanceRecord" + "\"" + " where" +
//                            " depart=~/" + depart + "/" +
//                            " and time >= '" + start + "' and time <= '" + end + "'"));
//        }
//        //有条件查询 时间区间
//        if (StringUtils.isEmpty(userName) && StringUtils.isEmpty(depart) && !StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
//            qr = influxDB.query(new Query(
//                    "select * from " + "\"" + "dayAttendanceRecord" + "\"" + " where " +
//                            "time >= '" + start + "' and time <= '" + end + "'"));
//        }
//        //有条件查询 用户模糊
//        if (!StringUtils.isEmpty(userName) && StringUtils.isEmpty(startTime) && StringUtils.isEmpty(endTime) && StringUtils.isEmpty(depart)) {
//            qr = influxDB.query(new Query(
//                    "select * from " + "\"" + "dayAttendanceRecord" + "\"" + " where userName=~/" + userName + "/"));
//        }
//        //有条件查询 部门模糊
//        if (!StringUtils.isEmpty(depart) && StringUtils.isEmpty(startTime) && StringUtils.isEmpty(endTime) && StringUtils.isEmpty(userName)) {
//            qr = influxDB.query(new Query(
//                    "select * from " + "\"" + "dayAttendanceRecord" + "\"" + " where depart=~/" + depart + "/"));
//        }
//        List<Map<String, Object>> dataList = new ArrayList<>();
//        List<String> columns = new ArrayList<>();
//        for (QueryResult.Result result : qr.getResults()) {
//            List<QueryResult.Series> series = result.getSeries();
//            if (series == null) {
//                return Result.ok();
//            }
//            for (QueryResult.Series serie : series) {
//                List<List<Object>> values = serie.getValues();//字段字集合
//                columns = serie.getColumns();//字段名
//                // 封装查询结果
//                for (int i = 0; i < values.size(); ++i) {
//                    Map<String, Object> dataMap = new HashMap<>(columns.size());
//                    for (int j = 0; j < columns.size(); ++j) {
//                        dataMap.put(columns.get(j), values.get(i).get(j));
//                    }
//                    dataList.add(dataMap);
//                }
//            }
//        }
//
//        Result<Object> result = new Result<>();
//        IPage<Map<String, Object>> paging = PageUtil.paging(pageNo, pageSize, dataList);
//        result.setResult(paging);
//        result.setSuccess(true);
//        return result;
//    }
//
//}
