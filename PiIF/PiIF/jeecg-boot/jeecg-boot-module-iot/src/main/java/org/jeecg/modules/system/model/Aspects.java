package org.jeecg.modules.system.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xduan
 * @version 2020/3/4
 * @desc 属性内部对象模型
 * 所有字段先赋默认值。
 */
@Data
public class Aspects implements Serializable {
    //是否只读
    private Boolean isReadOnly = false;
    //默认值
    // private String defaultValue = "0";

    private Boolean isPersistent = false;

    private Boolean isLogged = false;

    private String dataChangeType = "VALUE";

    private Integer cacheTime = 0;
    /**
     * 存储 dataShape 的name
     */
    private String dataShape;
}
