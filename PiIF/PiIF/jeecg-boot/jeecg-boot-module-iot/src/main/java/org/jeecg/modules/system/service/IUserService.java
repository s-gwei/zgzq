package org.jeecg.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.system.entity.SysUser;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @Author scott1
 * @since 2018-12-20
 */
public interface IUserService extends IService<SysUser> {

    /**
     * 删除用户
     *
     * @param userId
     * @return
     */
    public boolean deleteUser(String userId);


    /**
     * 添加用户和用户角色关系
     *
     * @param user
     * @param roles
     */
    public void addUserWithRole(SysUser user, String roles);


    /**
     * 添加用户和用户部门关系
     *
     * @param user
     * @param selectedParts
     */
    void addUserWithDepart(SysUser user, String selectedParts);

    /**
     * 查询普通用户角色id
     *
     * @return String
     */
    String findUserRoleId();

    /**
     * 查询管理角色id
     *
     * @return String
     */
    String findManagerRoleId();
}
