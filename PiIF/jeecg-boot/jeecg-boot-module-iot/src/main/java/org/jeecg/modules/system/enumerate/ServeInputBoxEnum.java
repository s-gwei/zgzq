package org.jeecg.modules.system.enumerate;

/**
 * @author zhang ho jian
 * @date 2020/9/30
 * @time 10:26
 * @Description : 服务用户输入框接收枚举
 */
public enum ServeInputBoxEnum {

    NOTESERVE("ReceptionPhone","短信服务"),
    EMAILSERVE("AddresseeEmail","邮件服务")

;
    private String  code;
    private String  value;

    ServeInputBoxEnum(String code, String value) {
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
    }
}
