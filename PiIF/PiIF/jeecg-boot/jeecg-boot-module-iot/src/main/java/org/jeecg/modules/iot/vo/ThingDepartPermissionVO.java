package org.jeecg.modules.iot.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xduan
 * @version 2020/4/3
 * 可见性权限实体
 */
@Data
public class ThingDepartPermissionVO implements Serializable {

    private static final long serialVersionUID = -6325251400672801591L;
    //部门id
    private String id;
    //部门名称
    private String departName;
    //是否继承
    private Boolean isExtend;

}
