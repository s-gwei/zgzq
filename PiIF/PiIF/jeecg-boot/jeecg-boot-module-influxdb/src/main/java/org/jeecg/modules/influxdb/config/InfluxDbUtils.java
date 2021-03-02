package org.jeecg.modules.influxdb.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;

@Data
@Slf4j
public class InfluxDbUtils {
    private String userName;
    private String password;
    private String url;
    public String database;
    private String retentionPolicy;
    // InfluxDB实例
    private InfluxDB influxDB;

    // 数据保存策略
    public static final String POLICY_NAME_PIX = "autogen";

    public InfluxDbUtils(String userName, String password, String url, String database,
                         String retentionPolicy) {
        this.userName = userName;
        this.password = password;
        this.url = url;
        this.database = database;
        this.retentionPolicy = retentionPolicy == null || "".equals(retentionPolicy) ? POLICY_NAME_PIX : retentionPolicy;
        this.influxDB = influxDbBuild();
    }

    /**
     * 连接数据库 ，若不存在则创建
     *
     * @return influxDb实例
     */
    private InfluxDB influxDbBuild() {
        if (influxDB == null) {
            influxDB = InfluxDBFactory.connect(url, userName, password);
        }
        try {
            influxDB.setDatabase(database);
        } catch (Exception e) {
            log.info(e.getMessage());
        } finally {
            influxDB.setRetentionPolicy(retentionPolicy);
        }
        influxDB.setLogLevel(InfluxDB.LogLevel.BASIC);
        return influxDB;
    }

}
