package org.jeecg.modules.datashape.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.datashape.entity.DataShapeColumn;
import org.jeecg.modules.datashape.entity.DataShapeModelVO;
import org.jeecg.modules.datashape.entity.IotDatashapeModel;
import org.jeecg.modules.datashape.service.IDatashapeModelService;
import org.jeecg.modules.datashape.service.ITableMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


/**
 * @version:2020/3/4
 * @Author:wzp
 * @Date:Created in 15:23 2020/3/4
 * @Description:influxdb表和列的crud
 */
@RestController
@RequestMapping("/tableMessage")
@Slf4j
public class DataShapeModelController {

    @Autowired
    private ITableMessageService tableMessageService;

    @Autowired
    private IDatashapeModelService datashapeModelService;


    /**
     * 根据条件模糊分页查询
     *
     * @param name        名称
     * @param description 描述
     * @param tags        标签
     * @param projectName 项目名
     * @param userId      当前用户id
     * @return Result
     */
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public Result<IPage<IotDatashapeModel>> query(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                  String name, String description, String tags, String projectName, String userId) {
        Result<IPage<IotDatashapeModel>> result = new Result<>();
        try {
            Page<IotDatashapeModel> page = new Page<>(pageNo, pageSize);
            IPage<IotDatashapeModel> datashapeModelIPage = datashapeModelService.query(page, name, description, tags, projectName, userId);
            result.setResult(datashapeModelIPage);
            result.setSuccess(true);
            result.setCode(CommonConstant.SC_OK_200);
        } catch (Exception e) {
            log.error(e.toString());
            result.error500(e.getMessage());
        }
        return result;
    }

    /**
     * 分页查询已经添加的表和列
     *
     * @return Result
     */
    @RequestMapping(value = "/findTableAndColumn", method = RequestMethod.GET)
    public Result<Object> findTableAndColumn(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Result<Object> result = new Result<>();
        try {
            result = tableMessageService.findTableAndColumn(pageNo, pageSize);
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
            result = tableMessageService.findTableAndColumnByTableName(tableName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 新增表
     *
     * @param dataShapeModelVO
     * @return Result
     */
    @ApiOperation("新增表")
    @RequestMapping(value = "/addTable", method = RequestMethod.POST)
    public Result<Object> addTable(@RequestBody DataShapeModelVO dataShapeModelVO) {
        Result<Object> result = new Result<>();
        try {
            //添加表
            result = tableMessageService.addTable(dataShapeModelVO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 新增列
     *
     * @param dataShapeColumn
     * @return Result
     */
    @ApiOperation("新增列")
    @RequestMapping(value = "/addTableColumn", method = RequestMethod.POST)
    public Result<Object> addTableColumn(@RequestBody DataShapeColumn dataShapeColumn) {
        Result<Object> result = new Result<>();
        try {
            Map<String, Object> map = (Map<String, Object>) dataShapeColumn.getColumn();
            String insertColumnName = (String) map.get("name");
            HashMap<String, Object> map1 = new HashMap<>();
            map1.put("user", dataShapeColumn.getUser());
            map1.put("name", dataShapeColumn.getTableName());
            map1.put(insertColumnName, map);
            //添加表
            result = tableMessageService.addTableColumn(JSON.toJSONString(map1));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 修改表
     *
     * @param dataShapeModelVO
     * @return Result
     */
    @ApiOperation("修改表")
    @RequestMapping(value = "/updateTable", method = RequestMethod.POST)
    public Result<Object> updateTable(@RequestBody DataShapeModelVO dataShapeModelVO) {
        Result<Object> result = new Result<>();
        try {
            //修改表
            result = datashapeModelService.updateTable(dataShapeModelVO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 修改列
     *
     * @param dataShapeColumn 全部列信息
     * @return Result
     */
    @ApiOperation("更新列")
    @RequestMapping(value = "/updateTableColumn", method = RequestMethod.POST)
    public Result<Object> updateTableColumn(@RequestBody DataShapeColumn dataShapeColumn) {
        Result<Object> result = new Result<>();
        try {
            Map<String, Object> map = (Map<String, Object>) dataShapeColumn.getColumn();
            String insertColumnName = (String) map.get("name");
            HashMap<String, Object> map1 = new HashMap<>();
            map1.put("user", dataShapeColumn.getUser());
            map1.put("name", dataShapeColumn.getTableName());
            map1.put("updateColumn", dataShapeColumn.getUpdateColumn());
            map1.put(insertColumnName, map);
            //更新列
            result = tableMessageService.updateTableColumn(JSON.toJSONString(map1));
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
            result = tableMessageService.deleteTableColumn(tableName, user, deleteColumn);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 删除表
     *
     * @param tableName 表名
     * @param user      删除人
     * @return Result
     */
    @RequestMapping(value = "/deleteTable", method = RequestMethod.POST)
    public Result<Object> deleteTable(String tableName, String user) {
        Result<Object> result = new Result<>();
        try {
            //删除列
            result = datashapeModelService.deleteTable(tableName, user);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

}
