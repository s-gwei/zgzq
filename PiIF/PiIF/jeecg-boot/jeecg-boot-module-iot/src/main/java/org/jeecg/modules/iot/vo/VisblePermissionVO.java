package org.jeecg.modules.iot.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xduan
 * @version 2020/4/8
 * @Desc 可见性权限vo对象
 */
@Data
public class VisblePermissionVO implements Serializable {

    private static final long serialVersionUID = -2084416494930409773L;
    //部门id
    private String departId;
    //控制对象对应的表名
    private String tableName;
    //可见性控制对象的 objectPk 字段
    private String objectPk;
}
