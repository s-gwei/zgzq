package org.jeecg.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author xduan
 * @version 2020/4/8
 * @Desc 可见性权限实体类
 */
@Data
@TableName("guardian_departobjectpermission")
public class GuardianDepartobjectermission implements Serializable {

    private static final long serialVersionUID = -3162638003441537560L;
    //id
    @TableId(type = IdType.AUTO)
    private Integer id;
    //所控制对象的主键
    private String objectPk;

    private String contentTypeId;
    //部门id
    private String departId;
    //记录是否是继承的权限
    private Boolean isExtend;
}
