package org.jeecg.modules.datashape.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.datashape.entity.DataShapeModelVO;
import org.jeecg.modules.datashape.entity.IotDatashapeModel;

import java.util.List;

/**
 * @Author:wzp
 * @Date:Created in 9:45 AM 2020/3/20
 * @Description:IDatashapeModelService
 */
public interface IDatashapeModelService extends IService<IotDatashapeModel> {

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
    IPage<IotDatashapeModel> query(Page<IotDatashapeModel> page, String name, String description, String tags, String projectName, String userId);


    /**
     * 添加表信息和操作人信息
     * @param subTableMessage 修改信息
     * @param tableMessage    列信息
     * @param tableName       表名
     */
    void addTableMessages(String subTableMessage, String tableMessage, String tableName);

    /**
     * 通过表名查询已有的列信息
     *
     * @param tableName 表名
     * @return IotDatashapeModel
     */
    IotDatashapeModel findExistColumns(String tableName);

    /**
     * 更新表新插入的列数据
     *
     * @param datashapeModel
     */
    int updateTableColumnAndMsg(IotDatashapeModel datashapeModel);

    /**
     * 根据表名查询表的最新列结构
     *
     * @param tableName 表名
     * @return String
     */
    IotDatashapeModel findLastedTableColumn(String tableName);

    /**
     * 查询已经添加的表和列
     *
     * @return Result
     */
    List<IotDatashapeModel> findTableAndColumn();

    /**
     * 根据表名查询已经添加的表和列
     *
     * @param tableName 表名
     * @return Result
     */
    IotDatashapeModel findTableAndColumnByTableName(String tableName);

    /**
     * 修改datashape
     *
     * @param dataShapeModelVO
     * @return Result
     */
    Result<Object> updateTable(DataShapeModelVO dataShapeModelVO);

    /**
     * 删除datashape
     *
     * @param tableName 表名
     * @param user      删除人
     * @return
     */
    Result<Object> deleteTable(String tableName, String user);
}
