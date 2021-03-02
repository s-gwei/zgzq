package com.pisx.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * @Description: mqtt属性
 * @author: boliu
 * @date: 2020年09月22日 9:20
 */
@Data
@ConfigurationProperties(prefix = "mqtt")
@Configuration
public class MqttProperties {
    /**
     * 本机ip作为clientid的后缀
     */
    private static String hostAddress;

    /**
     * 所有的配置
     */
    private final Map<String, Config> config;

    static {
        try {
            InetAddress address = InetAddress.getLocalHost();
            hostAddress = "_ip_" + address.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Data
    public static class Config {
        /**
         * 数组tcp://ip:port
         */
        private String[] url;
        /**
         * 超时时间，单位：秒
         */
        private int timeout;
        /**
         * 心跳时间，秒
         */
        private int kepAliveInterval;
        /**
         * qos设置，和topic一一对应
         */
        private int[] qos;
        /**
         * 主题，和qos一一对应
         */
        private String[] topics;
        /**
         * 账号
         */
        private String username;
        /**
         * 密码
         */
        private String password;
        /**
         * clientId后是否添加本机ip
         */
        private Boolean clientIdAppendIp;
        /**
         * consumer clientId
         */
        private String consumerClientId;
        /**
         * producer clientId
         */
        private String producerClientId;
        /**
         * 是否异步发送消息
         */
        private Boolean async;
        /**
         * consumer遗嘱配置
         */
        private Will consumerWill;
        /**
         * producer遗嘱配置
         */
        private Will producerWill;

        /**
         * 重写获取消费者客户端id
         *
         * @return
         */
        public String getConsumerClientId() {
            return clientIdAppendIp == null || !clientIdAppendIp ? consumerClientId : consumerClientId + hostAddress;
        }

        /**
         * 重写获生产者客户端id
         *
         * @return
         */
        public String getProducerClientId() {
            return clientIdAppendIp == null || !clientIdAppendIp ? producerClientId : producerClientId + hostAddress;
        }
    }

    @Data
    public static class Will {
        /**
         * 遗嘱qos设置
         */
        private int qos;
        /**
         * 遗嘱主题
         */
        private String topic;
        /**
         * 遗嘱内容
         */
        private String payload;
        /**
         * 是否发送保留消息
         */
        private Boolean retained;
    }
}
