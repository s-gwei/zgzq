package org.jeecg.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.system.entity.SysDepart;
import org.jeecg.modules.system.entity.SysRole;
import org.jeecg.modules.system.model.SysRoleSysDepartModel;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @Author scott
 * @since 2018-12-19
 */
public interface ISysRoleService extends IService<SysRole> {

    /**
     * 导入 excel ，检查 roleCode 的唯一性
     *
     * @param file
     * @param params
     * @return
     * @throws Exception
     */
    Result importExcelCheckRoleCode(MultipartFile file, ImportParams params) throws Exception;

    /**
     * 删除角色
     * @param roleid
     * @return
     */
    public boolean deleteRole(String roleid);

    /**
     * 批量删除角色
     * @param roleids
     * @return
     */
    public boolean deleteBatchRole(String[] roleids);

    /**
     * 根据部门Id查询
     * @param
     * @return
     */
    public IPage<SysRole> getRoleByDepartId(Page<SysRole> page, String departId, String roleName);

    public List<SysRole> getRoleByDepart(SysDepart depart);

    /**
     * 根据 orgCode 查询角色，包括子部门下的角色
     *
     * @param orgCode
     * @param roleParams 用户查询条件，可为空
     * @param page 分页参数
     * @return
     */
    IPage<SysRoleSysDepartModel> queryUserByOrgCode(String orgCode, SysRole roleParams, IPage page);

    /**
     * 根据名称获取角色
     * @param username
     * @return
     */
    List<SysRole> getRoleByName(String username);
}
