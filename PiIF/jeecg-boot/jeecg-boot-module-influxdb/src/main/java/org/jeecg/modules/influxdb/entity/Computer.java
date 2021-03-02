package org.jeecg.modules.influxdb.entity;

import lombok.Data;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import java.util.concurrent.TimeUnit;

@Data
@Measurement(name = "computer",timeUnit = TimeUnit.MILLISECONDS)
public class Computer extends BaseEntity{
    // 注解中添加tag = true,表示当前字段内容为tag内容
    @Column(name = "hum")
    private double hum;
    @Column(name = "cpu")
    private double cpu;
    @Column(name = "sn", tag = true)
    private String sn;
    @Column(name = "memory")
    private double memory;
}
