package com.pisx.emqxbackendpostgresql.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 消息实体
 * </p>
 *
 * @author boliu
 * @since 2020-10-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MqttMsg implements Serializable {

    private static final long serialVersionUID = 1L;

    private String fromUsername;

    private String fromClientId;

    private String topic;

    private Integer qos;

    private Integer retain;

    private String payload;

    private LocalDateTime ts;


}
