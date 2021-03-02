package org.jeecg.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.constant.CacheConstant;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.entity.SysUserDepart;
import org.jeecg.modules.system.entity.SysUserRole;
import org.jeecg.modules.system.mapper.SysUserRoleMapper;
import org.jeecg.modules.system.mapper.UserDepartMapper;
import org.jeecg.modules.system.mapper.UserMapper;
import org.jeecg.modules.system.mapper.UserRoleMapper;
import org.jeecg.modules.system.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @Author: scott1
 * @Date: 2018-12-20
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, SysUser> implements IUserService {


    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private UserDepartMapper userDepartMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;


    @Override
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(String userId) {
        //1.删除用户
        this.removeById(userId);
        //2.删除用户部门关联关系
        LambdaQueryWrapper<SysUserDepart> query = new LambdaQueryWrapper<SysUserDepart>();
        query.eq(SysUserDepart::getUserId, userId);
        userDepartMapper.delete(query);
        //3.删除用户角色关联关系
        //TODO
        return false;
    }


    @Override
    @Transactional
    public void addUserWithRole(SysUser user, String roles) {
        this.save(user);
        if (oConvertUtils.isNotEmpty(roles)) {
            String[] arr = roles.split(",");
            for (String roleId : arr) {
                SysUserRole userRole = new SysUserRole(user.getId(), roleId);
                sysUserRoleMapper.insert(userRole);
            }
        }
    }


    @Override
    @Transactional
    public void addUserWithDepart(SysUser user, String selectedParts) {
//		this.save(user);  //保存角色的时候已经添加过一次了
        if (oConvertUtils.isNotEmpty(selectedParts)) {
            String[] arr = selectedParts.split(",");
            for (String deaprtId : arr) {
                SysUserDepart userDeaprt = new SysUserDepart(user.getId(), deaprtId);
                userDepartMapper.insert(userDeaprt);
            }
        }
    }

    /**
     * 查询普通用户角色id
     *
     * @return String
     */
    @Override
    public String findUserRoleId() {
        return userRoleMapper.findUserRoleId();
    }

    /**
     * 查询管理角色id
     *
     * @return String
     */
    @Override
    public String findManagerRoleId() {
        return userRoleMapper.findManagerRoleId();
    }


}
