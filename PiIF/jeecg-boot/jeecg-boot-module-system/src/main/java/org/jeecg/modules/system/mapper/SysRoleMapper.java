package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.system.entity.SysRole;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.system.model.SysRoleSysDepartModel;

import java.util.List;

/**
 * <p>
 * 角色表 Mapper 接口
 * </p>
 *
 * @Author scott
 * @since 2018-12-19
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * @Author scott
     * @Date 2019/12/13 16:12
     * @Description: 删除角色与用户关系
     */
    @Delete("delete from sys_user_role where role_id = #{roleId}")
    void deleteRoleUserRelation(@Param("roleId") String roleId);


    /**
     * @Author scott
     * @Date 2019/12/13 16:12
     * @Description: 删除角色与权限关系
     */
    @Delete("delete from sys_role_permission where role_id = #{roleId}")
    void deleteRolePermissionRelation(@Param("roleId") String roleId);

    /**
     * 根据角色Id查询角色信息
     * @param page
     * @param
     * @return
     */
    IPage<SysRole> getRoleByDepartId(Page page, @Param("departId") String departId, @Param("roleName") String roleName);

    /**
     * 根据 orgCode 查询角色，包括子部门下的角色
     *
     * @param orgCode
     * @param roleParams 角色查询条件，可为空
     * @param page 分页参数
     * @return
     */
    List<SysRoleSysDepartModel> getRoleByOrgCode(IPage page, @Param("orgCode") String orgCode, @Param("roleParams") SysRole roleParams);

    /**
     * 查询 getRoleByOrgCode 的Total
     *
     * @param orgCode
     * @param roleParams 角色查询条件，可为空
     * @return
     */
    Integer getRoleByOrgCodeTotal(@Param("orgCode") String orgCode, @Param("roleParams") SysRole roleParams);

    List<SysRole> getRoleByName(@Param("username")String username);

    List<SysRole> getRoleByDepart(@Param("departId")String departId);
}
