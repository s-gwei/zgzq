package org.jeecg.modules.system.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.SpringContextUtils;
import org.jeecg.common.util.StringHandleUtil;
import org.jeecg.modules.influxdb.config.InfluxDbUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * @version:2020/3/4
 * @Author:wzp
 * @Date:Created in 15:22 2020/3/4
 * @Description:操作存储在mqtt的消息
 */
@Slf4j
@Component
public class MqttMessageUtil {


    /**
     * 根据表名查询对应消息集合
     *
     * @param tableName 表名
     * @param count     想要查询数量  没写就默认500
     * @return Result
     */
    public Result<Object> findMessageByTableName(String tableName, Integer count) throws Exception {
        InfluxDbUtils influxDbUtils = SpringContextUtils.getBean(InfluxDbUtils.class);
        //非空效验
        StringHandleUtil.validateParam("表名不存在", tableName);
        //默认查500条
        if (count == null) {
            count = 500;
        }
        //拼接sql
        String sql = "SELECT * FROM " + "\"" + tableName + "\"" + " order by time desc limit " + count;
        //查询
        QueryResult qr = influxDbUtils.getInfluxDB().query(new Query(sql));
        /**
         * 查询出来的数据结构
         * QueryResult [results=[Result [series=[Series [name=iotgateway/first, tags=null,
         * columns=[time, t, tt, ģ����ʾ��.����.Ramp3, ģ����ʾ��.����.Random6],
         * values=[[2020-03-12T16:04:26.645Z, 1.584000265066E12, 1584000265066, 894.0, -999602.0],
         * [2020-03-12T16:04:26.607Z, 1.584000257066E12, 1584000257066, 862.0, -985915.0]]]], error=null]],
         * error=null]
         */
        List<QueryResult.Result> results = qr.getResults();
        //key 列名 value 具体值
        HashMap<String, Object> hashMap;
        //封装最后的总数据
        ArrayList<HashMap<String, Object>> mapList = new ArrayList<>();
        //处理上面的数据结构
        for (QueryResult.Result result : results) {
            for (QueryResult.Series series : result.getSeries()) {
                //列名
                List<String> columns = series.getColumns();
                //查询出来的所有的value集合
                List<List<Object>> values = series.getValues();
                //放入我们自己定义的map
                for (List<Object> value : values) {
                    hashMap = new HashMap<>();
                    for (int i = 0; i < columns.size(); i++) {
                        hashMap.put(columns.get(i), value.get(i));
                    }
                    mapList.add(hashMap);
                }
            }
        }
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        result.setResult(JSON.toJSON(mapList));
        return result;
    }

    /**
     * 根据表名和列名查询对应消息集合
     *
     * @param tableName  表名
     * @param columnName 列名
     * @return Result
     */
//    public Result<Object> findMessageByTableNameAndColumnName(String tableName, String columnName) throws Exception {
//        //非空效验
//        StringHandleUtil.validateParam("表名或列名不存在", tableName, columnName);
//        //拼接sql
//        String sql = "SELECT " + columnName + " FROM " + "\"" + tableName + "\"" + " order by time desc";
//        //查询
//        QueryResult qr = influxDbUtils.getInfluxDB().query(new Query(sql));
//        /**
//         * 查询出来的数据结构
//         * QueryResult [results=[Result [series=[Series [name=disk, tags=null, columns=[time, free],
//         * values=[[2020-03-04T09:19:25.117Z, 1.0], [2020-03-04T09:14:56.034Z, 1.0]]]], error=null]], error=null]
//         */
//        List<QueryResult.Result> results = qr.getResults();
//        //key 列名 value 具体值
//        HashMap<String, Object> hashMap;
//        //封装最后的总数据
//        ArrayList<HashMap<String, Object>> mapList = new ArrayList<>();
//        //处理上面的数据结构
//        for (QueryResult.Result result : results) {
//            for (QueryResult.Series series : result.getSeries()) {
//                //列名
//                List<String> columns = series.getColumns();
//                //查询出来的所有的value集合
//                List<List<Object>> values = series.getValues();
//                //放入我们自己定义的map
//                for (List<Object> value : values) {
//                    hashMap = new HashMap<>();
//                    for (int i = 0; i < columns.size(); i++) {
//                        hashMap.put(columns.get(i), value.get(i));
//                    }
//                    mapList.add(hashMap);
//                }
//            }
//        }
//        Result<Object> result = new Result<>();
//        result.setSuccess(true);
//        result.setResult(JSON.toJSON(mapList));
//        return result;
//    }

    /**
     * 根据表名查询表的所有列名
     *
     * @param tableName 表名
     * @return Result
     */
//    public Result<Object> findColumnByTableName(String tableName) throws Exception {
//        //非空效验
//        StringHandleUtil.validateParam("表名不存在", tableName);
//        //拼接sql
//        String sql = "SELECT * FROM " + "\"" + tableName + "\"" + " limit 1";
//        //查询
//        QueryResult qr = influxDbUtils.getInfluxDB().query(new Query(sql));
//        List<QueryResult.Result> results = qr.getResults();
//        //列名
//        List<String> columns = new ArrayList<>();
//        //处理上面的数据结构
//        for (QueryResult.Result result : results) {
//            for (QueryResult.Series series : result.getSeries()) {
//                columns = series.getColumns();
//            }
//        }
//        Result<Object> result = new Result<>();
//        result.setSuccess(true);
//        result.setResult(JSON.toJSON(columns));
//        return result;
//    }

    /**
     * 存influxDB
     *
     * @param treeMap
     * @param topic
     */
    public static void insertInfluxDB(TreeMap<String, Object> treeMap, String topic, long time) {
        InfluxDbUtils influxDbUtils = SpringContextUtils.getBean(InfluxDbUtils.class);
        Point.Builder builder = Point.measurement(topic);
        builder.time(time, TimeUnit.MILLISECONDS);
        builder.fields(treeMap);
        influxDbUtils.getInfluxDB().write(builder.build());
    }
}
