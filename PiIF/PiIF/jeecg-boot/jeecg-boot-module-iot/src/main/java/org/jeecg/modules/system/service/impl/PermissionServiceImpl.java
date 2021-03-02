package org.jeecg.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.modules.iot.mapper.ThingDepartMapper;
import org.jeecg.modules.system.entity.SysUserDepart;
import org.jeecg.modules.system.mapper.UserDepartMapper;
import org.jeecg.modules.system.service.IPermissionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xduan
 * @version 2020/4/17
 * 权限相关的服务类
 */
@Service
public class PermissionServiceImpl implements IPermissionService {

    @Resource
    private ThingDepartMapper thingDepartMapper;


    @Resource
    private UserDepartMapper userDepartMapper;

    /**
     * 根据用户id查询所有关联部门id的集合
     *
     * @param userId
     * @return
     */
    @Override
    public List<String> getDepartList(String userId) {
        //关联的所有部门集合
        List<String> departIds = new ArrayList<>();
        //根据userId查询departId集合
        List<String> departs = thingDepartMapper.getDepartIdByUserId(userId);
        if (departs.size() > 0) {
            for (String depart : departs) {
                departIds.add(depart);
                getChildDepart(departIds, depart);
            }
        }
        //去重
        departIds = departIds.stream().distinct().collect(Collectors.toList());
        return departIds;
    }


    /**
     * 查询子部门的集合(包括本身）
     *
     * @param departIds
     * @param depart
     * @return
     */
    private void getChildDepart(List<String> departIds, String depart) {
        List<String> list = thingDepartMapper.queryChildDepart(depart);
        departIds.add(depart);
        if (list.size() > 0) {
            for (String departId : list) {
                departIds.add(departId);
                getChildDepart(departIds, departId);
            }
        }
    }

    /**
     * 根据用户id查询是否是管理员
     *
     * @return
     */
    @Override
    public boolean isAdmin(String userId) {
        List<String> username = thingDepartMapper.getUsernameByUserId(userId);
        if (username.size() == 0) {
            return false;
        }
        List<String> roleByUserName = thingDepartMapper.getRoleByUserName(username.get(0));
        return roleByUserName.contains("admin");
    }

    /**
     * 根据部门id查询所有的人员
     *
     * @param departId
     * @return
     */
    @Override
    public List<String> getUserIdList(String departId) {
        List<String> departIds = new ArrayList<>();
        getChildDepart(departIds, departId);
        //去重
        departIds = departIds.stream().distinct().collect(Collectors.toList());
        QueryWrapper<SysUserDepart> qw = new QueryWrapper<SysUserDepart>();
        qw.in("dep_id", departIds);
        List<SysUserDepart> sysUserDeparts = userDepartMapper.selectList(qw);
        return sysUserDeparts.stream().map(SysUserDepart::getUserId).collect(Collectors.toList());
    }
}
