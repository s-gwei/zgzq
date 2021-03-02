package org.jeecg.modules.system.mqtt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
//import com.taobao.api.ApiException;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.jeecg.modules.datashape.entity.IotTableColumn;
//import org.jeecg.modules.dingding.util.DingdingAttendanceRecords;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @version:2020/3/18
 * @Author:wzp
 * @Date:Created in 19:21 2020/3/18
 * @Description:
 */
public class Demo {

    public static void main(String[] args) {
//        InfluxDbUtils influxDbUtils = new InfluxDbUtils("admin", "admin", "http://121.37.166.26:8086", "pisx", "autogen");
//        InfluxDB influxDB = influxDbUtils.getInfluxDB();
//        HashMap<String, Object> map = new HashMap<>();
//        Object obj = 1223.1234f;
//        if (obj instanceof Number) {
//            if (obj instanceof Byte) {
//                obj = ((Byte) obj).doubleValue();
//            } else if (obj instanceof Short) {
//                obj = ((Short) obj).doubleValue();
//            } else if (obj instanceof Integer) {
//                obj = ((Integer) obj).doubleValue();
//            } else if (obj instanceof Long) {
//                obj = ((Long) obj).doubleValue();
//            } else if (obj instanceof BigInteger) {
//                obj = ((BigInteger) obj).doubleValue();
//            } else if (obj instanceof Float) {
//                obj = ((Float) obj).doubleValue();
//            }
//        }
//        map.put("ģ����ʾ��.����.Sine1", obj);
//        Point.Builder builder = Point.measurement("iotgateway/first");
//        builder.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
//        builder.fields(map);
//        influxDB.write(builder.build());

//        QueryResult results = influxDB.query(new Query("insert disk_free,hostname=server01 value=442221834240i 1435362189575692182"));
//        for (QueryResult.Result result : results) {
//            QueryResult.Series series = new QueryResult.Series();
//
//        }
        //results.getResults()是同时查询多条SQL语句的返回值
//        for (QueryResult.Result result : results.getResults()) {
//            List<QueryResult.Series> series = result.getSeries();
//            for (QueryResult.Series serie : series) {
//                List<List<Object>> values = serie.getValues();//字段字集合
//                List<String> colums = serie.getColumns();//字段名
//
//                // 打印查询结果
//                System.out.println("colums:" + colums);
//                for (List<Object> value : values) {
//                    System.out.println("value:" + value);
//                }
//
//                // 封装查询结果
//                List<Map<String, Object>> dataList = new LinkedList<>();
//                for (int i=0;i<values.size();++i){
//                    Map<String, Object> dataMap=new HashMap<>(colums.size());
//                    for (int j=0;j<colums.size();++j){
//                        dataMap.put(colums.get(j),values.get(i).get(j));
//                    }
//                    dataList.add(dataMap);
//                }
//
//                // dataList即可作为返回给用户的查询数据的基础格式
//                System.out.println(dataList);
//
//            }
//        }
//        BoundParameterQuery.QueryBuilder.newQuery("insert demo01,name").forDatabase("pisx");


//        createTable(influxDB);
//        QueryResult results = influxDB.query(new Query("select * from zpwang01"));
//
//
//        long l1 = System.currentTimeMillis();
//        long l = l1 + 28800000;
//        System.out.println(l);
//
//        influxDB.write(Point.measurement("zpwang01").time(l, TimeUnit.MILLISECONDS).addField("temp", "02").build());
//        QueryResult results1 = influxDB.query(new Query("delete from zpwang01 where time=" + l1 + "ms"));
        String tableName = "形状3";
//        QueryResult qr = influxDB.query(new Query("select * from " + "\"" + tableName + "\""));
//        for (QueryResult.Result result : qr.getResults()) {
//            List<QueryResult.Series> series = result.getSeries();
//            for (QueryResult.Series serie : series) {
//                List<List<Object>> values = serie.getValues();//字段字集合
//                List<String> colums = serie.getColumns();//字段名
//
//                // 打印查询结果
//                System.out.println("colums:" + colums);
//                for (List<Object> value : values) {
//                    System.out.println("value:" + value);
//                }
//
//                // 封装查询结果
//                List<Map<String, Object>> dataList = new LinkedList<>();
//                for (int i = 0; i < values.size(); ++i) {
//                    Map<String, Object> dataMap = new HashMap<>(colums.size());
//                    for (int j = 0; j < colums.size(); ++j) {
//                        dataMap.put(colums.get(j), values.get(i).get(j));
//                    }
//                    dataList.add(dataMap);
//                }
//
//                // dataList即可作为返回给用户的查询数据的基础格式
//                System.out.println(dataList);
//
//            }
//        }
//        influxDB.write(Point.measurement(tableName)
//                .time(System.currentTimeMillis() + 28800000, TimeUnit.MILLISECONDS)
//                .addField("333", 133.23)
//                .addField("字段", 145.1111)
//                .build());


//        BigDecimal divide = BigDecimal.valueOf(80).divide(BigDecimal.valueOf(50), 2, BigDecimal.ROUND_HALF_UP);

//        DingdingAttendanceRecords dingdingAttendanceRecords = new DingdingAttendanceRecords();
//        dingdingAttendanceRecords.getAttendanceData();


    }


    private static void createTable(InfluxDB influxDB) {
        String jsonData = "{\"user\":\"admin\",\n" +
                "\"times\":\"123454356\",\n" +
                "\"tableName\":\"demo01\",\n" +
                "\"Week\":{\"name\":\"Week\",\"aspects\":{},\"description\":\"\",\"baseType\":\"STRING\",\"ordinal\":2},\n" +
                "\"Day\":{\"name\":\"Day\",\"aspects\":{},\"description\":\"\",\"baseType\":\"STRING\",\"ordinal\":1}}";
        //处理json数据
        JSONObject jsonObject = JSON.parseObject(jsonData);
        //添加人
        String user = (String) jsonObject.get("user");
        //添加时间
        String times = (String) jsonObject.get("times");
        //添加表名
        String tableName = (String) jsonObject.get("tableName");
        jsonObject.remove("user");
        jsonObject.remove("times");
        jsonObject.remove("tableName");

        //拿到剩余所有的列
        Collection<Object> values = jsonObject.values();
        //通过spring容器拿到influx对象
//        MqttMessageStoreService storeService = SpringContextUtils.getBean(MqttMessageStoreService.class);
//        InfluxDB influxDB = storeService.getDbUtils().getInfluxDB();
        //
        List<IotTableColumn> iotTableColumns = new ArrayList<>();
        IotTableColumn iotTableColumn;
        for (Object value : values) {
            iotTableColumn = new IotTableColumn();
            JSONObject object = (JSONObject) value;
            iotTableColumn.setColumnName((String) object.get("name"));
            iotTableColumn.setColumnType((String) object.get("baseType"));
            iotTableColumn.setColumnOrder((Integer) object.get("ordinal"));
            iotTableColumns.add(iotTableColumn);
        }
        //排序
        iotTableColumns.sort(Comparator.comparing(IotTableColumn::getColumnOrder));
        for (IotTableColumn column : iotTableColumns) {
            if ("FLOAT".equals(column.getColumnType())
                    || "BYTE".equals(column.getColumnType())
                    || "SHORT".equals(column.getColumnType())
                    || "LONG".equals(column.getColumnType())
                    || "INTEGER".equals(column.getColumnType())
                    || "BIGINTEGER".equals(column.getColumnType())) {
                influxDB.write(Point.measurement(tableName)
                        .time(System.currentTimeMillis() + 28800000, TimeUnit.MILLISECONDS)
                        .addField(column.getColumnName(), Double.MIN_VALUE)
                        .build());
            } else if ("STRING".equals(column.getColumnType())) {
                influxDB.write(Point.measurement(tableName)
                        .time(System.currentTimeMillis() + 28800000, TimeUnit.MILLISECONDS)
                        .addField(column.getColumnName(), "")
                        .build());
            }
        }
    }
}
