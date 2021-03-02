package org.jeecg.modules.iot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author xduan
 * @version 2020/4/1
 * 权限关联表实体
 */
@Data
@TableName("guardian_roleobjectpermission")
public class GuardianRoleobjectpermission implements Serializable {
    private static final long serialVersionUID = -8534842548578626861L;

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String objectPk;

    private String permissionId;
    
    private String roleId;

    private Boolean isExtend;
}
