package org.jeecg.modules.system.service.impl;

import org.jeecg.modules.system.entity.SysDepart;
import org.jeecg.modules.system.entity.SysRole;
import org.jeecg.modules.system.entity.SysRoleDepart;
import org.jeecg.modules.system.mapper.SysRoleDepartMapper;
import org.jeecg.modules.system.service.ISysDepartService;
import org.jeecg.modules.system.service.ISysRoleDepartService;
import org.jeecg.modules.system.service.ISysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 角色关联部门
 * @Author: jeecg-boot
 * @Date:   2020-02-20
 * @Version: V1.0
 */
@Service
public class SysRoleDepartServiceImpl extends ServiceImpl<SysRoleDepartMapper, SysRoleDepart> implements ISysRoleDepartService {

    @Autowired
    private ISysDepartService departService;
    @Autowired
    private ISysRoleService roleService;
    /**
     * 查询所有角色对应的部门信息
     */
    @Override
    public Map<String, String> queryRoleDepart() {
        List<SysRoleDepart> uRoleList = this.list();
        List<SysDepart> departList = departService.list();
        List<SysRole> roleList = roleService.list();
        Map<String,String> map = new IdentityHashMap<>();
        String roleId = "";
        String departId = "";
        String departName = "";
        if(uRoleList != null && uRoleList.size() > 0) {
            for(SysRoleDepart uRole : uRoleList) {
                departId = uRole.getDepartId();
                for(SysRole role : roleList) {
                    roleId = role.getId();
                    if(uRole.getRoleId().equals(roleId)) {
                        departName = this.searchByRoleId(departList,departId);
                        map.put(roleId, departName);
                    }
                }
            }
            return map;
        }
        return map;
    }

    /**
     * queryRoleDepart
     * @param departList
     * @param departId
     * @return
     */
    private String searchByRoleId(List<SysDepart> departList, String departId) {
        while(true) {
            for(SysDepart depart : departList) {
                if(departId.equals(depart.getId())) {
                    return depart.getDepartName();
                }
            }
        }
    }
}
