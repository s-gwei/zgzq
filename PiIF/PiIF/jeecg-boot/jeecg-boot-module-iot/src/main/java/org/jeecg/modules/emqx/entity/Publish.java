package org.jeecg.modules.emqx.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xduan
 * @version 2020/3/25
 * 订阅的实体模型
 */
@Data
public class Publish implements Serializable {

    private static final long serialVersionUID = 8230927804106210994L;
    //主题
    private String topic;
    //
    private String payload;
    //
    private Integer qos;
    //是否持续
    private Boolean retain;
    //客户端id
    private String clientid;
}
