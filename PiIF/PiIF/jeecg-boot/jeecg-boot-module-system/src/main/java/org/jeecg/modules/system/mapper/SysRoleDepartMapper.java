package org.jeecg.modules.system.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.system.entity.SysRoleDepart;

/**
 * @Description: 角色关联部门
 * @Author: jeecg-boot
 * @Date:   2020-02-20
 * @Version: V1.0
 */
public interface SysRoleDepartMapper extends BaseMapper<SysRoleDepart> {

    @Select("select depart_name from sys_depart where id in (select depart_id from sys_role_depart where role_id = (select id from sys_role where role_name=#{roleName}))")
    List<String> getDepartByRoleName(@Param("roleName") String roleName);

    @Select("select id from sys_depart where id in (select depart_id from sys_role_depart where role_id = (select id from sys_role where role_name=#{roleName}))")
    List<String> getDepartIdByRoleName(@Param("roleName") String roleName);
}
