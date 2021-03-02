package org.jeecg.modules.iot.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.iot.service.ISysIotRuntimePermissionService;
import org.jeecg.modules.iot.util.PageUtil;
import org.jeecg.modules.iot.vo.IotPermissionVO;
import org.jeecg.modules.iot.vo.RuntimePermissionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author sliu
 * @description 物和物模板运行时权限controller类
 * @date 2020/3/9
 */
@Slf4j
@RestController
@RequestMapping("/iot")
@Api(tags = "SysIotRuntimePermissionController")
public class SysIotRuntimePermissionController {

    @Autowired
    private ISysIotRuntimePermissionService sysIotPermissionService;


    /**
     * 获取拥有物权限的用户列表
     *
     * @param tableName 表名
     * @param thingId   物id   <p>注意对于设备是主键，但是对于其他对象默认是object_pk</p>
     * @return IotPermissionVO列表
     */
    @ApiOperation("查询物或者物模板编辑权限的用户列表")
    @RequestMapping(value = "/queryIotEditRightUserList", method = RequestMethod.GET)
    public Result<IPage<IotPermissionVO>> queryIotEditRightUserList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                                    String tableName, String thingId) {
        Result<IPage<IotPermissionVO>> result = new Result<IPage<IotPermissionVO>>();
        List<IotPermissionVO> iotPermissionVOList = sysIotPermissionService.queryIotEditRightUserList(tableName,
                thingId);
        IPage<IotPermissionVO> page = PageUtil.paging(pageNo, pageSize, iotPermissionVOList);
        result.setResult(page);
        return result;
    }

    /**
     * 设置用户或者组的权限，若需要添加且没有该权限，则新增数据，否则报错。若不需要则删除。给已有权限再次置为true会报错，但没有权限再置为false不会报错
     * isChangeable和isDeleteable类型为布尔包装类，不填值则为null，不会报错
     *
     * @return 是否设置成功
     */
    @ApiOperation("修改物或者物模板编辑权限")
    @RequestMapping(value = "/changeIotEditRight", method = RequestMethod.PUT)
    public Result<?> changeIotEditRight(@RequestBody RuntimePermissionVO runtimePermission) {
        try {
            if (runtimePermission.getIsExtend()) {
                return Result.error("继承的权限不能修改");
            }
            boolean b = sysIotPermissionService.changeIotEditRight(runtimePermission.getIsUser(), runtimePermission.getPersonId()
                    , runtimePermission.getTableName(), runtimePermission.getThingId(), runtimePermission.getIsChangeable(),
                    runtimePermission.getIsDeleteable());
        } catch (JeecgBootException e) {
            log.error(e.toString());
            return Result.error(e.getMessage());
        }
        return Result.ok("修改成功");
    }

    /**
     * 获取用户对单个物的权限列表
     *
     * @param isUser    是否为用户或者组
     * @param personId  人员id,对应用户id或者组id
     * @param tableName 表名
     * @param thingId   物id   <p>注意对于设备是主键，但是对于其他对象默认是object_pk</p>
     * @return 权限列表
     */
    @ApiOperation("获取用户对单个物的权限列表")
    @RequestMapping(value = "/getIotEditRight", method = RequestMethod.GET)
    public Result<List<String>> getIotEditRight(boolean isUser, String personId, String tableName, String thingId) {
        Result<List<String>> result = new Result<>();
        List<String> rightList = sysIotPermissionService.getIotEditRight(isUser, personId, tableName, thingId);
        result.setSuccess(true);
        result.setResult(rightList);
        return result;
    }


}
