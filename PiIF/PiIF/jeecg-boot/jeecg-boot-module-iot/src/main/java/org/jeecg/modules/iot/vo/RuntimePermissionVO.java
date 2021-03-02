package org.jeecg.modules.iot.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xduan
 * @version 2020/4/2
 * @Desc 运行时权限VO
 */
@Data
public class RuntimePermissionVO implements Serializable {

    private static final long serialVersionUID = 542796820743753496L;
    //用户或角色id
    private String personId;
    //对象表名称
    private String tableName;
    //对象id， <p>注意对于设备是主键，但是对于其他对象默认是object_pk</p>
    private String thingId;
    //是否是用户
    private Boolean isUser;
    //是否具备删除权限
    private Boolean isDeleteable;
    //是否具备修改权限
    private Boolean isChangeable;
    //是否是继承的权限,默认是false
    private Boolean isExtend = false;
}
