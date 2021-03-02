package org.jeecg.modules.emqx.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pisx.utils.InfluxDbConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class MqttMessageChannel1Handler implements MessageHandler {

    @Autowired
    InfluxDbConnection influxDBConnection;

    @ServiceActivator(inputChannel = "channel1")
    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        Map<String, String> tags = new HashMap<>();
        JSONObject jsonObject = JSON.parseObject(String.valueOf(message.getPayload()));
        if (jsonObject.containsKey("values")) {
            JSONArray jsonArray = (JSONArray) jsonObject.get("values");
            if (!jsonArray.isEmpty()) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    Map fields = JSON.parseObject(jsonArray.getJSONObject(i).toJSONString(), Map.class);
                    influxDBConnection.insert("iot", tags, fields);
                }
            }
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