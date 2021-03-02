package org.jeecg.modules.system.enumerate;

/**
 * @author zhang ho jian
 * @date 2020/9/30
 * @time 10:21
 * @Description :订阅服务类型
 */
public enum ServeTypeEnum {

    NOTE("001","短信服务"),
    EMAIL("002","邮件服务")

    ;
    private String  code;
    private String  value;

    ServeTypeEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }}
