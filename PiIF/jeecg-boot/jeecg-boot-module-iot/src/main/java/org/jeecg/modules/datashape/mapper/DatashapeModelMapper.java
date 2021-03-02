package org.jeecg.modules.datashape.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.datashape.entity.IotDatashapeModel;

import java.util.List;


/**
 * @version:2020/3/4
 * @Author:wzp
 * @Date:Created in 15:23 2020/3/4
 * @Description: datashape_model mapper
 */
public interface DatashapeModelMapper extends BaseMapper<IotDatashapeModel> {

    /**
     * 添加表信息和操作人信息
     *
     * @param tableType    修改信息
     * @param tableMessage 列信息
     * @param tableName    表名
     */
    void addTableMessages(@Param("tableType") String tableType, @Param("tableMessage") String tableMessage, @Param("name") String tableName);

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
    int updateTableColumn(@Param("datashapeModel") IotDatashapeModel datashapeModel);

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
     * @return List
     */
    List<IotDatashapeModel> findTableAndColumn();

    /**
     * 根据表名查询已经添加的表和列
     *
     * @param tableName 表名
     * @return IotDatashapeModel
     */
    IotDatashapeModel findTableAndColumnByTableName(String tableName);

    /**
     * 根据id修改datashape表
     *
     * @param datashapeModel
     * @return int
     */
    int updateTable(@Param("datashapeModel") IotDatashapeModel datashapeModel);

    /**
     * 根据id查询datashape
     *
     * @param id
     * @return IotDatashapeModel
     */
    IotDatashapeModel findById(int id);

    /**
     * 连表查询datashape
     *
     * @param tableName 表名
     * @return IotDatashapeModel
     */
    IotDatashapeModel findWithValueStreamModel(String tableName);

    /**
     * 删除datashape
     *
     * @param datashapeModel
     */
    int deleteTable(@Param("datashapeModel") IotDatashapeModel datashapeModel);
}
