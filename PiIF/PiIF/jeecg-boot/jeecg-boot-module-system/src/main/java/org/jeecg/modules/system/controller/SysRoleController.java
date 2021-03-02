package org.jeecg.modules.system.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.system.entity.*;
import org.jeecg.modules.system.model.SysRoleSysDepartModel;
import org.jeecg.modules.system.model.TreeModel;
import org.jeecg.modules.system.service.*;
import org.jeecg.modules.system.vo.SysRoleDepartVO;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *
 * @Author scott
 * @since 2018-12-19
 */
@RestController
@RequestMapping("/sys/role")
@Slf4j
@Api(tags = "SysRoleController")
public class SysRoleController {
    @Autowired
    private ISysRoleService sysRoleService;

    @Autowired
    private ISysPermissionDataRuleService sysPermissionDataRuleService;

    @Autowired
    private ISysRolePermissionService sysRolePermissionService;

    @Autowired
    private ISysPermissionService sysPermissionService;

    @Autowired
    private ISysRoleDepartService sysRoleDepartService;

    @Autowired
    ISysUserService sysUserService;

    @Autowired
    private ISysDepartService sysDepartService;

    /**
     * 分页列表查询
     *
     * @param role
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<SysRole>> queryPageList(SysRole role,
                                                @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                HttpServletRequest req) {
        Result<IPage<SysRole>> result = new Result<IPage<SysRole>>();
        QueryWrapper<SysRole> queryWrapper = QueryGenerator.initQueryWrapper(role, req.getParameterMap());
        Page<SysRole> page = new Page<SysRole>(pageNo, pageSize);
        IPage<SysRole> pageList = sysRoleService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     *
     * @param role
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<SysRole> add(@RequestBody SysRole role) {
        Result<SysRole> result = new Result<SysRole>();
        try {
            role.setCreateTime(new Date());
            sysRoleService.save(role);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 编辑
     *
     * @param role
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    public Result<SysRole> edit(@RequestBody SysRole role) {
        Result<SysRole> result = new Result<SysRole>();
        SysRole sysrole = sysRoleService.getById(role.getId());
        if (sysrole == null) {
            result.error500("未找到对应实体");
        } else {
            role.setUpdateTime(new Date());
            boolean ok = sysRoleService.updateById(role);
            //TODO 返回false说明什么？
            if (ok) {
                result.success("修改成功!");
            }
        }

        return result;
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        sysRoleService.deleteRole(id);
        return Result.ok("删除角色成功");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
    public Result<SysRole> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<SysRole> result = new Result<SysRole>();
        if (oConvertUtils.isEmpty(ids)) {
            result.error500("未选中角色！");
        } else {
            sysRoleService.deleteBatchRole(ids.split(","));
            result.success("删除角色成功!");
        }
        return result;
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/queryById", method = RequestMethod.GET)
    public Result<SysRole> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<SysRole> result = new Result<SysRole>();
        SysRole sysrole = sysRoleService.getById(id);
        if (sysrole == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(sysrole);
            result.setSuccess(true);
        }
        return result;
    }

    @RequestMapping(value = "/queryall", method = RequestMethod.GET)
    public Result<List<SysRole>> queryall() {
        Result<List<SysRole>> result = new Result<>();
        List<SysRole> list = sysRoleService.list();
        if (list == null || list.size() <= 0) {
            result.error500("未找到角色信息");
        } else {
            result.setResult(list);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 校验角色编码唯一
     */
    @RequestMapping(value = "/checkRoleCode", method = RequestMethod.GET)
    public Result<Boolean> checkUsername(String id, String roleCode) {
        Result<Boolean> result = new Result<>();
        result.setResult(true);//如果此参数为false则程序发生异常
        log.info("--验证角色编码是否唯一---id:" + id + "--roleCode:" + roleCode);
        try {
            SysRole role = null;
            if (oConvertUtils.isNotEmpty(id)) {
                role = sysRoleService.getById(id);
            }
            SysRole newRole = sysRoleService.getOne(new QueryWrapper<SysRole>().lambda().eq(SysRole::getRoleCode, roleCode));
            if (newRole != null) {
                //如果根据传入的roleCode查询到信息了，那么就需要做校验了。
                if (role == null) {
                    //role为空=>新增模式=>只要roleCode存在则返回false
                    result.setSuccess(false);
                    result.setMessage("角色编码已存在");
                    return result;
                } else if (!id.equals(newRole.getId())) {
                    //否则=>编辑模式=>判断两者ID是否一致-
                    result.setSuccess(false);
                    result.setMessage("角色编码已存在");
                    return result;
                }
            }
        } catch (Exception e) {
            result.setSuccess(false);
            result.setResult(false);
            result.setMessage(e.getMessage());
            return result;
        }
        result.setSuccess(true);
        return result;
    }

    /**
     * 导出excel
     *
     * @param request
     * @param request
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(SysRole sysRole, HttpServletRequest request) {
        // Step.1 组装查询条件
        QueryWrapper<SysRole> queryWrapper = QueryGenerator.initQueryWrapper(sysRole, request.getParameterMap());
        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<SysRole> pageList = sysRoleService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "角色列表");
        mv.addObject(NormalExcelConstants.CLASS, SysRole.class);
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("角色列表数据", "导出人:" + user.getRealname(), "导出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                return sysRoleService.importExcelCheckRoleCode(file, params);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return Result.error("文件导入失败！");
    }

    /**
     * 查询数据规则数据
     */
    @GetMapping(value = "/datarule/{permissionId}/{roleId}")
    public Result<?> loadDatarule(@PathVariable("permissionId") String permissionId, @PathVariable("roleId") String roleId) {
        List<SysPermissionDataRule> list = sysPermissionDataRuleService.getPermRuleListByPermId(permissionId);
        if (list == null || list.size() == 0) {
            return Result.error("未找到权限配置信息");
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("datarule", list);
            LambdaQueryWrapper<SysRolePermission> query = new LambdaQueryWrapper<SysRolePermission>()
                .eq(SysRolePermission::getPermissionId, permissionId)
                .eq(SysRolePermission::getRoleId, roleId);
            SysRolePermission sysRolePermission = sysRolePermissionService.getOne(query);
            if (sysRolePermission == null) {
                //return Result.error("未找到角色菜单配置信息");
            } else {
                String drChecked = sysRolePermission.getDataRuleIds();
                if (oConvertUtils.isNotEmpty(drChecked)) {
                    map.put("drChecked", drChecked.endsWith(",") ? drChecked.substring(0, drChecked.length() - 1) : drChecked);
                }
            }
            return Result.ok(map);
            //TODO 以后按钮权限的查询也走这个请求 无非在map中多加两个key
        }
    }

    /**
     * 保存数据规则至角色菜单关联表
     */
    @PostMapping(value = "/datarule")
    public Result<?> saveDatarule(@RequestBody JSONObject jsonObject) {
        try {
            String permissionId = jsonObject.getString("permissionId");
            String roleId = jsonObject.getString("roleId");
            String dataRuleIds = jsonObject.getString("dataRuleIds");
            log.info("保存数据规则>>" + "菜单ID:" + permissionId + "角色ID:" + roleId + "数据权限ID:" + dataRuleIds);
            LambdaQueryWrapper<SysRolePermission> query = new LambdaQueryWrapper<SysRolePermission>()
                .eq(SysRolePermission::getPermissionId, permissionId)
                .eq(SysRolePermission::getRoleId, roleId);
            SysRolePermission sysRolePermission = sysRolePermissionService.getOne(query);
            if (sysRolePermission == null) {
                return Result.error("请先保存角色菜单权限!");
            } else {
                sysRolePermission.setDataRuleIds(dataRuleIds);
                this.sysRolePermissionService.updateById(sysRolePermission);
            }
        } catch (Exception e) {
            log.error("SysRoleController.saveDatarule()发生异常：" + e.getMessage(), e);
            return Result.error("保存失败");
        }
        return Result.ok("保存成功!");
    }


    /**
     * 用户角色授权功能，查询菜单权限树
     *
     * @param request
     * @return

    @ApiOperation("查询用户的权限树")
    @RequestMapping(value = "/queryTreeList", method = RequestMethod.GET)
    public Result<Map<String, Object>> queryTreeList(HttpServletRequest request) {
        Result<Map<String, Object>> result = new Result<>();
        //全部权限ids
        List<String> ids = new ArrayList<>();
        List<SysPermission> list = new ArrayList<>();
        try {


            //1.获取用户名
            //String token = request.getHeader("X-Access-Token");
            //String username = JwtUtil.getUsername(token);
            String username="test";
            if (username.equals("admin")){
                LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<SysPermission>();
                query.eq(SysPermission::getDelFlag, CommonConstant.DEL_FLAG_0);
                query.orderByAsc(SysPermission::getSortNo);
                list = sysPermissionService.list(query);
            }else {
                //根据所拥有的角色获取的权限
                List<SysPermission> perList_1 = sysPermissionService.queryByUser(username);
                //根据所在部门获取的权限
                //1.根据用户名获取所在的部门集合
                SysUser sysUser = sysUserService.getUserByName(username);
                List<SysDepart> departs = sysDepartService.queryUserDeparts(sysUser.getId());
                //2.根据用户获取所属的角色集合
                List<SysRole> roles = sysRoleService.getRoleByName(username);
                //3.根据部门找部门下的角色集合
                List<SysRole> roleList = new ArrayList<>();
                roleList.addAll(roles);
                if (departs.size()>0){
                    for (SysDepart depart : departs) {
                        List<SysRole> sysRoles=sysRoleService.getRoleByDepart(depart);
                        roleList.addAll(sysRoles);
                    }
                }
                //4.根据角色集合查找权限
                List<SysPermission> perList_2=new ArrayList<>();
                if (roleList.size()>0){
                    for (SysRole sysRole : roleList) {
                        String roleId = sysRole.getId();
                        List<SysPermission> permissions=sysPermissionService.queryByRoleId(roleId);
                        perList_2.addAll(permissions);
                    }
                }
                //合并权限
                list.addAll(perList_1);
                list.addAll(perList_2);
                //权限去重
                list=init(list);
            }
            for (SysPermission sysPer : list) {
                ids.add(sysPer.getId());
            }
            List<TreeModel> treeList = new ArrayList<>();
            getTreeModelList(treeList, list, null);
            Map<String, Object> resMap = new HashMap<String, Object>();
            resMap.put("treeList", treeList); //全部树节点数据
            resMap.put("ids", ids);//全部树ids
            result.setResult(resMap);
            result.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }*/
    /**
     * 权限列表去重
     * @param metaList
     * @return
     */
    private List<SysPermission> init(List<SysPermission> metaList) {
        List<SysPermission> list=new ArrayList<>();
        List<String> ids=new ArrayList<>();
        for (SysPermission sysPermission : metaList) {
            String id = sysPermission.getId();
            if (id!=null&&!ids.contains(id)){
                ids.add(id);
                list.add(sysPermission);
            }
        }
        return list;
    }

    /**
     * 接口备份
     *
     * 用户角色授权功能，查询菜单权限树
     * @param request
     * @return
     */
     @RequestMapping(value = "/queryTreeList", method = RequestMethod.GET)
     public Result<Map<String,Object>> queryTreeList(HttpServletRequest request) {
     Result<Map<String,Object>> result = new Result<>();
     //全部权限ids
     List<String> ids = new ArrayList<>();
     try {
     LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<SysPermission>();
     query.eq(SysPermission::getDelFlag, CommonConstant.DEL_FLAG_0);
     query.orderByAsc(SysPermission::getSortNo);
     List<SysPermission> list = sysPermissionService.list(query);
     for(SysPermission sysPer : list) {
     ids.add(sysPer.getId());
     }
     List<TreeModel> treeList = new ArrayList<>();
     getTreeModelList(treeList, list, null);
     Map<String,Object> resMap = new HashMap<String,Object>();
     resMap.put("treeList", treeList); //全部树节点数据
     resMap.put("ids", ids);//全部树ids
     result.setResult(resMap);
     result.setSuccess(true);
     } catch (Exception e) {
     log.error(e.getMessage(), e);
     }
     return result;
     }

    /**
     * add by zgzhang
     * 给指定部门添加角色
     *
     * @param
     * @return
     */
    @ApiOperation("测试给部门添加角色")
    @RequestMapping(value = "/addSysRoleDepart", method = RequestMethod.POST)
    public Result<String> addSysRoleDepart(@RequestBody SysRoleDepartVO sysRoleDepartVO) {
        Result<String> result = new Result<String>();
        try {
            String sysDepartId = sysRoleDepartVO.getDepartId();
            for (String sysRoleId : sysRoleDepartVO.getRoleIdList()) {
                SysRoleDepart sysRoleDepart = new SysRoleDepart(sysRoleId, sysDepartId);
                QueryWrapper<SysRoleDepart> queryWrapper = new QueryWrapper<SysRoleDepart>();
                queryWrapper.eq("depart_id", sysDepartId).eq("role_id", sysRoleId);
                SysRoleDepart one = sysRoleDepartService.getOne(queryWrapper);
                if (one == null) {
                    sysRoleDepartService.save(sysRoleDepart);
                }

            }
            result.setMessage("添加成功!");
            result.setSuccess(true);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("出错了: " + e.getMessage());
            return result;
        }
    }

    /**
     * add by zgzhang
     * 查询指定部门的角色
     *
     * @param pageNo 页码，默认为1
     * @param pageSize 每页显示数量，默认为10
     * @param req 用来获取部门id
     * @return
     */
    @ApiOperation("测试查询指定部门的角色")
    @RequestMapping(value = "/roleDepartList", method = RequestMethod.GET)
    public Result<IPage<SysRole>> roleDepartList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                 HttpServletRequest req) {
        Result<IPage<SysRole>> result = new Result<IPage<SysRole>>();
        Page<SysRole> page = new Page<SysRole>(pageNo, pageSize);
        String departId = req.getParameter("departId");
        String roleName = req.getParameter("roleName");
        IPage<SysRole> pageList = sysRoleService.getRoleByDepartId(page, departId, roleName);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * add by zgzhang
     * 根据 orgCode 查询角色，包括子部门下的角色
     * 若某个角色包含多个部门，则会显示多条记录，可自行处理成单条记录
     */
    @ApiOperation("根据 orgCode 查询角色")
    @GetMapping("/queryByOrgCode")
    public Result<?> queryByDepartId(
        @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
        @RequestParam(name = "orgCode") String orgCode,
        SysRole roleParams
    ) {
        IPage<SysRoleSysDepartModel> pageList = sysRoleService.queryUserByOrgCode(orgCode, roleParams, new Page(pageNo, pageSize));
        return Result.ok(pageList);
    }

    /**
     * add by zgzhang
     * @param departId 部门id
     * @param roleId 角色id
     *
     * 删除指定部门的角色关系
     */
    @ApiOperation("删除指定部门的角色关系")
    @RequestMapping(value = "/deleteRoleInDepart", method = RequestMethod.DELETE)
    public Result<SysRoleDepart> deleteRoleInDepart(@RequestParam(name = "departId") String departId,
                                                    @RequestParam(name = "roleId", required = true) String roleId
    ) {
        Result<SysRoleDepart> result = new Result<SysRoleDepart>();
        try {
            QueryWrapper<SysRoleDepart> queryWrapper = new QueryWrapper<SysRoleDepart>();
            queryWrapper.eq("depart_id", departId).eq("role_id", roleId);
            sysRoleDepartService.remove(queryWrapper);
            result.success("删除成功!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("删除失败！");
        }
        return result;
    }

    private void getTreeModelList(List<TreeModel> treeList, List<SysPermission> metaList, TreeModel temp) {
        for (SysPermission permission : metaList) {
            String tempPid = permission.getParentId();
            TreeModel tree = new TreeModel(permission.getId(), tempPid, permission.getName(), permission.getRuleFlag(), permission.isLeaf());
            if (temp == null && oConvertUtils.isEmpty(tempPid)) {
                treeList.add(tree);
                if (!tree.getIsLeaf()) {
                    getTreeModelList(treeList, metaList, tree);
                }
            } else if (temp != null && tempPid != null && tempPid.equals(temp.getKey())) {
                temp.getChildren().add(tree);
                if (!tree.getIsLeaf()) {
                    getTreeModelList(treeList, metaList, tree);
                }
            }

        }
    }


}
