package org.jeecg.modules.system.enumerate;

/**
 * @author zhang ho jian
 * @date 2020/9/16
 * @time 9:31
 * @Description :定义事件的匹配规制
 */
public enum DataTypeRuleEnum {
    EQUALTO("equalTo","1"),
    NOTEQUALTO("notEqualTo","2"),
    ABOVE("above","3"),
    UNDER("under","4"),
    INRANGE("inRange","5"),
    OUTOFRANGE("outofRange","6"),
    DEVIATIONABOVE("deviationAbove","7"),
    DEVIATIONBELOW("deviationBelow","8")

    ;

    private String  code;
    private String  value;

    DataTypeRuleEnum(String code, String value) {
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
