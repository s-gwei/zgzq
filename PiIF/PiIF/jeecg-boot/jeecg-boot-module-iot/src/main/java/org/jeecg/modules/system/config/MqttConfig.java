package org.jeecg.modules.system.config;

import org.jeecg.modules.system.mqtt.MqttPushClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @version:2020/3/4
 * @Author:wzp
 * @Date:Created in 15:22 2020/3/4
 * @Description:mqtt信息接收端
 */
@Configuration
public class MqttConfig {
    /**
     * 代理服务器ip地址
     */
    @Value("${mqtt.url}")
    public String MQTT_BROKER_HOST;

    /**
     * qos
     */
    @Value("${mqtt.qos}")
    public int QOS;

    /**
     * topic
     */
    private static final String TOPIC = "iotgateway/#";


    @Bean
    public void startMqttPushClient() {
        MqttPushClient.MQTT_HOST = MQTT_BROKER_HOST;
        MqttPushClient.MQTT_CLIENTID = System.currentTimeMillis() + "";
        MqttPushClient instance = MqttPushClient.getInstance();
        instance.subscribe(TOPIC, QOS);
    }

}
