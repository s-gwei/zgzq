package org.jeecg.modules.valuestream.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.valuestream.entity.IotValuestreamModel;
import org.jeecg.modules.valuestream.entity.ValueStreamColumn;
import org.jeecg.modules.valuestream.entity.ValueStreamModelVO;

/**
 * @Author:wzp
 * @Date:Created in 9:45 AM 2020/3/20
 * @Description:IStreamModelService
 */
public interface IValueStreamModelService extends IService<IotValuestreamModel> {

    /**
     * 条件查询
     *
     * @param page
     * @param name
     * @param description
     * @param tags
     * @param projectName
     * @param userId
     * @return
     */
    IPage<IotValuestreamModel> query(Page<IotValuestreamModel> page, String name, String description, String tags, String projectName, String userId);


    /**
     * 新增valueStreamModel
     *
     * @param valueStreamModelVO
     * @return Result
     */
    Result<Object> add(ValueStreamModelVO valueStreamModelVO);

    /**
     * 新增valueStreamModel列
     *
     * @param valueStreamColumn
     * @return Result
     */
    Result<Object> addColumns(ValueStreamColumn valueStreamColumn);

    /**
     * 查询已经添加的表和列
     *
     * @return Result
     */
    Result<Object> findTableAndColumn(Integer pageNo, Integer pageSize);

    /**
     * 根据表名查询已经添加的表和列
     *
     * @param tableName 表名
     * @return Result
     */
    Result<Object> findTableAndColumnByTableName(String tableName);

    /**
     * 修改valueStreamModel
     *
     * @param valueStreamModelVO
     * @return Result
     */
    Result<Object> updateTable(ValueStreamModelVO valueStreamModelVO);

    /**
     * 修改valueStreamModel列属性
     *
     * @param valueStreamColumn
     * @return Result
     */
    Result<Object> updateTableColumn(ValueStreamColumn valueStreamColumn);

    /**
     * 删除列
     *
     * @param tableName    表名
     * @param user         删除人
     * @param deleteColumn 删除列
     * @return Result
     */
    Result<Object> deleteTableColumn(String tableName, String user, String deleteColumn);

    /**
     * 删除表
     *
     * @param tableName
     * @param user
     * @return
     */
    Result<Object> deleteTable(String tableName, String user);

    //    /**
//     * influxdb列添加数据
//     *
//     * @param tableName   表名
//     * @param column      添加数据的列
//     * @param columnValue 具体数据
//     * @param valueType   列值的类型
//     * @param user        添加人
//     * @return Result
//     */
//    Result<Object> updateColumnValue(String tableName, String column, String columnValue, String valueType, String user);
//
//    /**
//     * 查询列的历史数据
//     *
//     * @param tableName 表名
//     * @param column    列名
//     * @return Result
//     */
//    Result<Object> findUpdateColumnValues(String tableName, String column);
//    /**
//     * 根据表名和列名查询ValuestreamModel
//     *
//     * @param tableName 表名
//     * @param column    列名
//     * @return IotValuestreamModel1
//     */
//    IotValuestreamModel1 findExistColumnByTableNameAndColumn(String tableName, String column);
//
//    /**
//     * 更新修改信息
//     *
//     * @param existValueStreamModelIot
//     */
//    void updateConfigurationChanges(IotValuestreamModel1 existValueStreamModelIot);
}
