package com.pisx.emqxbackendmysql.entity;

import java.time.LocalDateTime;
import java.sql.Blob;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author boliu
 * @since 2020-10-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MqttMsg implements Serializable {

    private static final long serialVersionUID = 1L;

    private String fromClientId;

    private String topic;

    private String fromUsername;

    private Boolean qos;

    private Boolean retain;

    private String payload;

    private LocalDateTime ts;


}
