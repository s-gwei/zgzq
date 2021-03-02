package com.pisx.emqxbackendinfluxdb.constant;

/**
 * @Description: webhook常量
 * @author: boliu
 * @date: 2020年10月09日 15:00
 */
public class WebHookConstant {
    /**
     * 要持久化的topic
     */
    public static final String TOPIC = "iotgateway/";

    /**
     * 消息的时间戳
     */
    public static final String TS = "ts";
    /**
     * qos 等级
     */
    public static final String QOS = "qos";
    /**
     * 消息来源用户名
     */
    public static final String FROM_USERNAME = "from_username";
    /**
     * 消息clientid
     */
    public static final String FROM_CLIENT_ID = "from_client_id";
    /**
     * 上传数据
     */
    public static final String MESSAGE_PUBLISH = "message_publish";
}
