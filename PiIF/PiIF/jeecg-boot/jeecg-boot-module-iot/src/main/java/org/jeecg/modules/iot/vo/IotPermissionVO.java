package org.jeecg.modules.iot.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author sliu
 * @description 用户权限视图对象
 * @date 2020/3/11
 */
@Data
public class IotPermissionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户或组id
     */
    private String id;

    /**
     * 用户或者组的名称
     */
    private String name;

    /**
     * 是否可编辑
     */
    private boolean isChangeable;

    /**
     * 是否可删除
     */
    private boolean isDeleteable;

    /**
     * 是否是用户，标识用户和组
     */
    private boolean isUser;

    //是否是继承的权限
    private Boolean isExtend;
}
