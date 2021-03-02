package com.pisx.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * @Description: mqtt工具类，根据通道名称发送消息
 * @author: boliu
 * @date: 2020年09月22日 10:34
 */
public class MqttUtils {

    private static final Logger logger = LoggerFactory.getLogger(MqttUtils.class);
    /**
     * qos 0
     */
    public static final int QOS_0 = 0;
    /**
     * qos 1
     */
    public static final int QOS_1 = 1;
    /**
     * qos 2
     */
    public static final int QOS_2 = 2;


    public static final String CHANNEL_NAME_SUFFLX = "MqttPahoMessageHandler";
    private static final Map<String, MqttPahoMessageHandler> handlerMap = new HashMap<>();

    public static void put(String channelName, MqttPahoMessageHandler handler) {
        handlerMap.put(channelName + CHANNEL_NAME_SUFFLX, handler);
    }

    /**
     * 发送消息
     *
     * @param topic
     * @param message
     * @param qos
     * @param channelName
     */
    public static void sendMessage(String topic, String message, int qos, String channelName) {
        MqttPahoMessageHandler handler = getHandler(channelName);
        Message<String> mqttMessage = MessageBuilder.withPayload(message).setHeader(MqttHeaders.TOPIC, topic)
                .setHeader(MqttHeaders.QOS, qos).build();
        handler.handleMessage(mqttMessage);
    }


    /**
     * 发送消息，默认qos级别为1
     *
     * @param topic
     * @param message
     * @param channelName
     */
    public static void sendMessage(String topic, String message, String channelName) {
        MqttPahoMessageHandler handler = getHandler(channelName);
        Message<String> mqttMessage = MessageBuilder.withPayload(message).setHeader(MqttHeaders.TOPIC, topic)
                .setHeader(MqttHeaders.QOS, QOS_1).build();
        handler.handleMessage(mqttMessage);
    }

    /**
     * 发送消息到指定的通道
     *
     * @param mqttMessage
     * @param channelName
     */
    public static void sendMessage(Message<String> mqttMessage, String channelName) {
        MqttPahoMessageHandler handler = getHandler(channelName);
        handler.handleMessage(mqttMessage);
    }

    /**
     * 只有一个通道，通过该通道发送消息
     *
     * @param topic
     * @param message
     * @param qos
     */
    public static void sendMessage(String topic, String message, int qos) {
        MqttPahoMessageHandler handler = getDefaultHeadler();
        Message<String> mqttMessage = MessageBuilder.withPayload(message).setHeader(MqttHeaders.TOPIC, topic)
                .setHeader(MqttHeaders.QOS, qos).build();
        handler.handleMessage(mqttMessage);
    }

    /**
     * 只有一个通道，通过该通道发送消息，默认qos级别为1
     *
     * @param topic
     * @param message
     */
    public static void sendMessage(String topic, String message) {
        MqttPahoMessageHandler handler = getDefaultHeadler();
        Message<String> mqttMessage = MessageBuilder.withPayload(message).setHeader(MqttHeaders.TOPIC, topic)
                .setHeader(MqttHeaders.QOS, QOS_1).build();
        handler.handleMessage(mqttMessage);
    }

    /**
     * 如果只有一个通道将使用该通道发送消息，默认qos级别为1
     *
     * @param mqttMessage
     */
    public static void sendMessage(Message<String> mqttMessage) {
        MqttPahoMessageHandler handler = getDefaultHeadler();
        handler.handleMessage(mqttMessage);
    }

    /**
     * 获取默认的handler
     *
     * @return
     */
    private static MqttPahoMessageHandler getDefaultHeadler() {
        Collection<MqttPahoMessageHandler> values = handlerMap.values();
        Iterator<MqttPahoMessageHandler> iterator = values.iterator();
        MqttPahoMessageHandler handler = iterator.next();
        if (null == handler) {
            logger.error("发送消息失败，无可用的handler");
        }
        return handler;
    }

    /**
     * 根据通道名称获取handler
     *
     * @param channelName
     */
    private static MqttPahoMessageHandler getHandler(String channelName) {
        MqttPahoMessageHandler handler = handlerMap.get(channelName + CHANNEL_NAME_SUFFLX);
        if (null == handler) {
            logger.error("未查询到通道{}对应的handler,通道名称{}", channelName, handlerMap.keySet());
        }
        return handler;
    }

}
