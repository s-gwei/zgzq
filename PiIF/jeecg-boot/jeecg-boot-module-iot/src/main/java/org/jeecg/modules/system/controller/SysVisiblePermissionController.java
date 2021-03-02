package org.jeecg.modules.system.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.iot.entity.SysDepart;
import org.jeecg.modules.iot.vo.VisblePermissionVO;
import org.jeecg.modules.system.service.ISysVisiblePermissionService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author xduan
 * @version 2020/4/8
 * @Desc （对象级）可见性权限接口
 */
@Slf4j
@RestController
@RequestMapping("/sys/visiblePermission")
@Api("可见性权限")
public class SysVisiblePermissionController {

    @Resource
    private ISysVisiblePermissionService visiblePermissionService;


    @PostMapping(value = "/add")
    @ApiOperation("新增可见性权限")
    public Result<?> add(@RequestBody VisblePermissionVO visblePermissionVO) {
        try {
            visiblePermissionService.add(visblePermissionVO);
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error(e.getMessage());
        }
        return Result.ok("新增成功");
    }

    /**
     * 根据部门id，表名，对象的objectPk字段删除可见性权限
     *
     * @param departId
     * @param tableName
     * @param objectPk
     * @return
     */
    @DeleteMapping(value = "/delete")
    @ApiOperation("删除可见性权限")
    public Result<?> delete(String departId, String tableName, String objectPk) {
        try {
            visiblePermissionService.delete(departId, objectPk, tableName);
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error(e.getMessage());
        }
        return Result.ok("删除成功");
    }

    /**
     * 根据表名和用户id查询对应的对象列表
     *
     * @param pageNo
     * @param pageSize
     * @param tableName 表名
     * @param userId    用户id
     * @return
     */
    @GetMapping(value = "/getByUserId")
    @ApiOperation("可见性列表查询")
    public Result<IPage<Object>> getByUserId(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                             String tableName, String userId) {
        Result<IPage<Object>> result = new Result<IPage<Object>>();
        try {
            Page<Object> page = new Page<Object>(pageNo, pageSize);
            IPage<Object> pageList = visiblePermissionService.getByUserId(page, userId, tableName);
            result.setSuccess(true);
            result.setResult(pageList);
        } catch (Exception e) {
            log.error(e.toString());
            return result.error500(e.getMessage());
        }
        return result;
    }

    /**
     * 返回可查看当前对象的所有部门
     * @param pageNo
     * @param pageSize
     * @param tableName
     * @param objectPk
     * @return
     */
    @GetMapping(value = "/getByObjectPk")
    @ApiOperation("对象可见性权限查询")
    public Result<IPage<SysDepart>> getByObjectPk(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                  String tableName, String objectPk) {
        Result<IPage<SysDepart>> result = new Result<IPage<SysDepart>>();
        try {
            Page<SysDepart> page = new Page<SysDepart>(pageNo, pageSize);
            IPage<SysDepart> pageList = visiblePermissionService.getByObjectPk(page, objectPk, tableName);
            result.setSuccess(true);
            result.setResult(pageList);
        } catch (Exception e) {
            log.error(e.toString());
            return result.error500(e.getMessage());
        }
        return result;
    }
}
