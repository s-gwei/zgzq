package org.jeecg.modules.emqx.handler;

import cn.hutool.core.lang.Filter;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.pisx.utils.InfluxDbConnection;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.system.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
@SuppressWarnings("unchecked")
public class MqttMessageChannel2Handler implements MessageHandler {
    private static final String DEVICE_PROPERTY_DATA_TOPIC_PREFIX = "iotgateway/";

    @Autowired
    private MonitorService monitorService;

    @Autowired
    private InfluxDbConnection influxDbConnection;

    @ServiceActivator(inputChannel = "channel2")
    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        String mqttReceivedTopic = message.getHeaders().get("mqtt_receivedTopic").toString();
        JSONObject payloadObject = JSONUtil.parseObj(message.getPayload().toString());
        Map<String, Object> propertyMap = DataFormatter.formatData(payloadObject.toString());
        //处理从kepware传过来的数据
        if (mqttReceivedTopic.startsWith(DEVICE_PROPERTY_DATA_TOPIC_PREFIX)) {
            Map<String, String> tags = new HashMap<>(16);
            if (propertyMap.containsKey("values")) {
                List<Map<String,Object>> propertiesList = (List<Map<String, Object>>) propertyMap.get("values");
                if (!propertiesList.isEmpty()) {
                    for (int i = 0; i < propertiesList.size(); i++) {
                        Map<String, Object> fields = propertiesList.get(i);
                        Map<String, Object> fieldsMonitorMap = MapUtil.filter(fields, (Filter<Map.Entry<String, Object>>) k -> !k.getKey().equals("t"));
                        monitorService.monitor(fieldsMonitorMap, mqttReceivedTopic);
                        influxDbConnection.insert(mqttReceivedTopic, tags, fields);
                    }
                }
            }
        }
    }

}