package org.jeecg.modules.system.enumerate;

/**
 * @author zhang ho jian
 * @date 2020/9/21
 * @time 10:44
 * @Description : 启用状态枚举类
 */
public enum InitiateStateEnum {

    start("启用","1"),
    forbid("禁用","2");

    private String code;
    private  String value;


    InitiateStateEnum(String code, String value) {
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
