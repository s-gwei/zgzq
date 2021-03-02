package org.jeecg.modules.system.enumerate;

/**
 * @author zhang ho jian
 * @date 2020/9/8
 * @time 15:27
 * @Description :警告消息状态枚举
 */
public enum WarnNewsStateEnum {

    UNCONFIRMED("unconfirmed","未确认"),
    CONFIRMED("confirmed","已确认");

    private String code;
    private String value;

    WarnNewsStateEnum(String code, String value) {
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
