package org.jeecg.modules.system.mqtt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.jeecg.modules.system.entity.ReceiveMessage;
import org.jeecg.modules.system.util.MqttMessageUtil;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.*;

/**
 * @version:2020/3/4
 * @Author:wzp
 * @Date:Created in 15:23 2020/3/4
 * @Description:mqtt回调
 */
@Component
@Slf4j
public class MqttPushCallback implements MqttCallback {

    @Override
    public void connectionLost(Throwable cause) {
        log.info("连接断开,正在尝试重新连接");
        cause.printStackTrace();
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

    /**
     * 处理接收到的消息
     *
     * @param topic
     * @param message
     * @throws Exception
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        try {
            log.info("Topic: " + topic);
            log.info("Message: " + new String(message.getPayload()));
            //处理json数据
            JSONObject jsonObject = JSON.parseObject(new String(message.getPayload()));
            //直接根据key的值取到value
            JSONArray jsonArray = (JSONArray) jsonObject.get("values");
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
            //存入influxDB
            for (Long timeStamp : map.keySet()) {
                List<ReceiveMessage> sameTimeList = map.get(timeStamp);
                for (ReceiveMessage sameTimeMessage : sameTimeList) {
                    treeMap.put(sameTimeMessage.getId(), changeNumberType(sameTimeMessage.getV()));
                    treeMap.put("t", sameTimeMessage.getT());
                }
                MqttMessageUtil.insertInfluxDB(treeMap, topic, timeStamp);
                treeMap = new TreeMap<>();
            }
        } catch (Exception e) {
            throw new Exception(e);
        }

    }

    /**
     * 数字转换成double类型
     *
     * @param obj
     * @return Obj
     */
    private Object changeNumberType(Object obj) {
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
