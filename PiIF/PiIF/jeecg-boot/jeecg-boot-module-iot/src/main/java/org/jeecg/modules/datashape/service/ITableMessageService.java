package org.jeecg.modules.datashape.service;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.datashape.entity.DataShapeModelVO;

/**
 * @version:2020/3/4
 * @Author:wzp
 * @Date:Created in 15:23 2020/3/4
 * @Description:ITableMessageService表信息接口
 */
public interface ITableMessageService {

    /**
     * 新增表
     *
     * @param dataShapeModelVO
     * @return
     */
    Result<Object> addTable(DataShapeModelVO dataShapeModelVO) throws Exception;

    /**
     * 新增列
     *
     * @param jsonData
     * @return Result
     */
    Result<Object> addTableColumn(String jsonData);

    /**
     * 删除列
     * @param tableName 表名
     * @param user 删除人
     * @param deleteColumn 删除列
     * @return Result
     */
    Result<Object> deleteTableColumn(String tableName, String user, String deleteColumn);

    /**
     * 更新列
     *
     * @param jsonData
     * @return Result
     */
    Result<Object> updateTableColumn(String jsonData);


    /**
     * 查询已经添加的表和列
     *
     * @param pageNo
     * @param pageSize
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
}
