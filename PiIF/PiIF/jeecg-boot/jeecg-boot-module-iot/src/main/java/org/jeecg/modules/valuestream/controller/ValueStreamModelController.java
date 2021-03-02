package org.jeecg.modules.valuestream.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.valuestream.entity.IotValuestreamModel;
import org.jeecg.modules.valuestream.entity.ValueStreamColumn;
import org.jeecg.modules.valuestream.entity.ValueStreamModelVO;
import org.jeecg.modules.valuestream.service.IValueStreamModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @version:2020/3/4
 * @Author:wzp
 * @Date:Created in 15:23 2020/3/4
 * @Description:valueStreamModel的crud
 */
@RestController
@RequestMapping("/valueStreamModel")
@Slf4j
public class ValueStreamModelController {

    @Autowired
    private IValueStreamModelService valueStreamModelService;

    /**
     * 根据条件模糊查询
     *
     * @param name
     * @param description
     * @param tags
     * @param projectName
     * @param userId      当前用户id
     * @return Result
     */
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public Result<IPage<IotValuestreamModel>> query(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                    String name, String description, String tags, String projectName, String userId) {
        Result<IPage<IotValuestreamModel>> result = new Result<>();
        try {
            Page<IotValuestreamModel> page = new Page<>(pageNo, pageSize);
            IPage<IotValuestreamModel> streamModelIPage = valueStreamModelService.query(page, name, description, tags, projectName, userId);
            result.setResult(streamModelIPage);
            result.setSuccess(true);
            result.setCode(CommonConstant.SC_OK_200);
        } catch (Exception e) {
            log.error(e.toString());
            result.error500(e.getMessage());
        }
        return result;
    }

    /**
     * 查询已经添加的表和列
     *
     * @return Result
     */
    @RequestMapping(value = "/findTableAndColumn", method = RequestMethod.GET)
    public Result<Object> findTableAndColumn(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Result<Object> result = new Result<>();
        try {
            result = valueStreamModelService.findTableAndColumn(pageNo, pageSize);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 根据表名查询已经添加的表和列
     *
     * @param tableName 表名
     * @return Result
     */
    @RequestMapping(value = "/findTableAndColumnByTableName", method = RequestMethod.GET)
    public Result<Object> findTableAndColumnByTableName(String tableName) {
        Result<Object> result = new Result<>();
        try {
            result = valueStreamModelService.findTableAndColumnByTableName(tableName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 新增valueStreamModel
     *
     * @param valueStreamModelVO
     * @return Result
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<Object> add(@RequestBody ValueStreamModelVO valueStreamModelVO) {
        Result<Object> result = new Result<>();
        try {
            result = valueStreamModelService.add(valueStreamModelVO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }


    /**
     * 新增valueStreamModel列
     *
     * @param valueStreamColumn
     * @return Result
     */
    @RequestMapping(value = "/addColumns", method = RequestMethod.POST)
    public Result<Object> addColumns(@RequestBody ValueStreamColumn valueStreamColumn) {
        Result<Object> result = new Result<>();
        try {
            result = valueStreamModelService.addColumns(valueStreamColumn);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 修改valueStreamModel
     *
     * @param valueStreamModelVO
     * @return Result
     */
    @RequestMapping(value = "/updateTable", method = RequestMethod.POST)
    public Result<Object> updateTable(@RequestBody ValueStreamModelVO valueStreamModelVO) {
        Result<Object> result = new Result<>();
        try {
            //修改表
            result = valueStreamModelService.updateTable(valueStreamModelVO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 修改valueStreamModel列属性
     *
     * @param valueStreamColumn
     * @return Result
     */
    @RequestMapping(value = "/updateTableColumn", method = RequestMethod.POST)
    public Result<Object> updateTableColumn(@RequestBody ValueStreamColumn valueStreamColumn) {
        Result<Object> result = new Result<>();
        try {
            //更新列
            result = valueStreamModelService.updateTableColumn(valueStreamColumn);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 删除列
     *
     * @param tableName    表名
     * @param user         删除人
     * @param deleteColumn 删除列
     * @return Result
     */
    @RequestMapping(value = "/deleteTableColumn", method = RequestMethod.POST)
    public Result<Object> deleteTableColumn(String tableName, String user, String deleteColumn) {
        Result<Object> result = new Result<>();
        try {
            //删除列
            result = valueStreamModelService.deleteTableColumn(tableName, user, deleteColumn);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 删除valueStream
     *
     * @param tableName 表名
     * @param user      删除人
     * @return Result
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Result<Object> delete(String tableName, String user) {
        Result<Object> result = new Result<>();
        try {
            //删除valueStream
            result = valueStreamModelService.deleteTable(tableName, user);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

//    /**
//     * influxdb列添加数据
//     *
//     * @param tableName   表名
//     * @param column      添加数据的列
//     * @param columnValue 具体数据
//     * @param user        添加人
//     * @return Result
//     */
//    @ApiOperation("更新列值")
//    @RequestMapping(value = "/updateColumnValue", method = RequestMethod.POST)
//    public Result<Object> updateColumnValue(@Param("name") String tableName, @Param("column") String column, @Param("columnValue") String columnValue, @Param("valueType") String valueType, @Param("user") String user) {
//        Result<Object> result = new Result<>();
//        try {
//            //非空效验
//            StringHandleUtil.validateParam("数据异常,请联系管理员", tableName, column);
//            //添加表
//            result = tableMessageService.updateColumnValue(tableName, column, columnValue, valueType, user);
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        }
//        return result;
//    }
//
//    /**
//     * 查询修改数据
//     *
//     * @param tableName 表名
//     * @param column    查询列
//     * @return Result
//     */
//    @ApiOperation("查询列的历史值")
//    @RequestMapping(value = "/findUpdateColumnValues", method = RequestMethod.POST)
//    public Result<Object> findUpdateColumnValues(@Param("name") String tableName, @Param("column") String column) {
//        Result<Object> result = new Result<>();
//        try {
//            //非空效验
//            StringHandleUtil.validateParam("数据异常,请联系管理员", tableName, column);
//            //添加表
//            result = tableMessageService.findUpdateColumnValues(tableName, column);
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        }
//        return result;
//    }
}
