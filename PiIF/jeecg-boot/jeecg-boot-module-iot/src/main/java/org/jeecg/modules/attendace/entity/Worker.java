package org.jeecg.modules.attendace.entity;

import lombok.Data;

/**
 * @Author:wzp
 * @Date:Created in 11:44 AM 2020/4/25
 * @Description:门禁人员entity
 */
@Data
public class Worker {

    /**
     * 员工姓名
     */
    private String name;

    /**
     * 身份 普通用户/管理员
     */
    private String auth;

    /**
     * 员工id
     */
    private String ccid;

    /**
     * 刷卡卡号
     */
    private String card;

    /**
     * 传过来的数据类型  员工/部门
     */
    private String data;

    /**
     * 部门
     */
    private String deptid;

    /**
     * 操作类型  更新/删除
     */
    private String doType;

    /**
     * 是否有人脸
     */
    private String faceexist;

    /**
     * 主键id
     */
    private String id;

    /**
     * 打卡密码
     */
    private String passwd;


}
