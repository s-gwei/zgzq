package org.jeecg.modules.influxdb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author cluo
 * @Data 2020年2月6号
 * @Description influxdb的配置类
 * @Email cluo@pisx.com
 */
@Configuration
public class InfluxDBConfig {
    @Value("${spring.influx.user}")
    private String userName;

    @Value("${spring.influx.password}")
    private String password;

    @Value("${spring.influx.url}")
    private String url;

    @Value("${spring.influx.database}")
    private String database;

    @Bean
    public InfluxDbUtils influxDbUtils() {
        return new InfluxDbUtils(userName, password, url, database, InfluxDbUtils.POLICY_NAME_PIX);
    }
}
