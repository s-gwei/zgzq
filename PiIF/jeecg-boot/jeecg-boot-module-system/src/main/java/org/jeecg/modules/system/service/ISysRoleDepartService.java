package org.jeecg.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.system.entity.SysRoleDepart;

import java.util.Map;

/**
 * @Description: 角色关联部门
 * @Author: jeecg-boot
 * @Date:   2020-02-20
 * @Version: V1.0
 */
public interface ISysRoleDepartService extends IService<SysRoleDepart> {
    /**
     * 查询所有的角色部门信息
     * @return
     */
    Map<String,String> queryRoleDepart();
}
