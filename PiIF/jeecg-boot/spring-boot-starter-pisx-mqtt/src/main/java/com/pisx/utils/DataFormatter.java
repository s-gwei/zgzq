package com.pisx.utils;

import com.pisx.model.ReceiveMessage;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.math.BigInteger;
import java.util.*;

/**
 * @Description: 数据格式化工具
 * @author: boliu
 * @date: 2020年10月12日 15:52
 */
public class DataFormatter {

    public static Map<String, Object> formatData(String message) {
        //处理json数据
        JSONObject jsonObject = JSON.parseObject(message);
        //直接根据key的值取到value
        JSONArray jsonArray = (JSONArray) jsonObject.get("values");
        //获取timestamp
        Object timestamp = jsonObject.get("timestamp");
        //处理接收到的消息
        ReceiveMessage receiveMessage;
        List<ReceiveMessage> receiveMessages = new ArrayList<>();
        //存入influxDB的数据,tags map
        TreeMap<String, Object> treeMap = new TreeMap<>();
        //key是timestamp  value是相同timestamp的消息
        Map<Long, List<ReceiveMessage>> map = new HashMap<>();
        //从jsonArray转成自己定义的集合
        for (int i = 0; i < jsonArray.size(); i++) {
            receiveMessage = JSON.toJavaObject(jsonArray.getJSONObject(i), ReceiveMessage.class);
            receiveMessages.add(receiveMessage);
        }
        //处理成为我们需要的map格式,相同timestamp的放一起
        for (ReceiveMessage receiveMessage1 : receiveMessages) {
            Long timeStamp = receiveMessage1.getT();
            if (map.containsKey(timeStamp)) {
                map.get(timeStamp).add(receiveMessage1);
            } else {
                ArrayList<ReceiveMessage> sameList1 = new ArrayList<>();
                sameList1.add(receiveMessage1);
                map.put(timeStamp, sameList1);
            }
        }
        //格式化存入influxDB的数据
        Map<String, Object> dataFormattedMap = new HashMap<>();
        List<TreeMap<String, Object>> treeMapList = new ArrayList<>();
        for (Long timeStamp : map.keySet()) {
            List<ReceiveMessage> sameTimeList = map.get(timeStamp);
            for (ReceiveMessage sameTimeMessage : sameTimeList) {
                treeMap.put(sameTimeMessage.getId(), changeNumberType(sameTimeMessage.getV()));
                treeMap.put("t", sameTimeMessage.getT());
            }
            treeMapList.add(treeMap);
            dataFormattedMap.put("values", treeMapList);
            dataFormattedMap.put("timeStamp", timeStamp);
            treeMap = new TreeMap<>();
        }
        return dataFormattedMap;
    }

    /**
     * 数字转换成double类型
     *
     * @param obj
     * @return Obj
     */
    private static Object changeNumberType(Object obj) {
        if (obj instanceof Number) {
            if (obj instanceof Byte) {
                obj = ((Byte) obj).doubleValue();
            } else if (obj instanceof Short) {
                obj = ((Short) obj).doubleValue();
            } else if (obj instanceof Integer) {
                obj = ((Integer) obj).doubleValue();
            } else if (obj instanceof Long) {
                obj = ((Long) obj).doubleValue();
            } else if (obj instanceof BigInteger) {
                obj = ((BigInteger) obj).doubleValue();
            } else if (obj instanceof Float) {
                obj = ((Float) obj).doubleValue();
            }
        }
        return obj;
    }
}
