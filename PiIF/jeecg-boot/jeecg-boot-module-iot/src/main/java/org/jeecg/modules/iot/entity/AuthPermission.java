package org.jeecg.modules.iot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author xduan
 * @version 2020/4/1
 */
@Data
@TableName("auth_permission")
public class AuthPermission implements Serializable {
    private static final long serialVersionUID = -5962447977683941859L;

    @TableId(type = IdType.ID_WORKER_STR)
    private String id;
    //权限名
    private String name;

    private String contentTypeId;
    //代码名
    private String codename;
}
