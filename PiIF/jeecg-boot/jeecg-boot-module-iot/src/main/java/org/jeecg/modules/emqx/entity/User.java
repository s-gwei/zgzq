package org.jeecg.modules.emqx.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xduan
 * @version 2020/3/18
 * @desc emqx用户对象
 */
@Data
public class User implements Serializable {
    private static final long serialVersionUID = 5598979684862924875L;
    //用户名
    private String username;
    //密码
    private String password;
    //标签，备注
    private String tags;
    //新密码
    private String newPassword;
    //重复确认密码
    private String repeatPassword;
}
