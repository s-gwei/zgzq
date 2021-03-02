package com.pisx.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: influxdb属性
 * @author: boliu
 * @date: 2020年09月24日 15:28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InfluxDbProperties {
    private String url;
    private String userName;
    private String password;
    private String database;
    private String retentionPolicy = "autogen";
    private String retentionPolicyTime = "30d";
    private int actions = 2000;
    private int flushDuration = 1000;
    private int jitterDuration = 0;
    private int bufferLimit = 10000;
}
