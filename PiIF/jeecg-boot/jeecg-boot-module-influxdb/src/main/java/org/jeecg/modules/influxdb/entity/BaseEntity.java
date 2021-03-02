package org.jeecg.modules.influxdb.entity;

import lombok.Data;
import org.influxdb.annotation.Column;

@Data
public class BaseEntity {
    /**
     * 用户登陆账号
     */
    @Column(name = "sys_user_code", tag = true)
    private String sys_user_code;
    /**
     * 用户名
     */
    @Column(name = "sys_user_name")
    private String sys_user_name;
    /**
     * 组织编码
     */
    @Column(name = "sys_org_code", tag = true)
    private String sys_org_code;
    /**
     * 多组织逗号隔开
     */
    @Column(name = "sysMultiOrgCode")
    private String sysMultiOrgCode;

}
