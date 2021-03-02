package org.jeecg.modules.valuestream.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.valuestream.entity.IotValuestreamModel;

import java.util.List;

/**
 * @Author:wzp
 * @Date:Created in 9:45 AM 2020/3/20
 * @Description:StreamModelMapper
 */
public interface ValueStreamModelMapper extends BaseMapper<IotValuestreamModel> {

    /**
     * 查询表是否已经存在
     *
     * @param tableName 表名
     * @return IotValuestreamModel
     */
    IotValuestreamModel findByTableName(String tableName);

    /**
     * 修改valuestreamModel
     *
     * @param valuestreamModel
     * @return int
     */
    int updateValueStreamModel(@Param("valuestreamModel") IotValuestreamModel valuestreamModel);

    /**
     * 查询全部IotValuestreamModel
     *
     * @return List
     */
    List<IotValuestreamModel> findTableAndColumn();

    /**
     * 根据表名查询IotValuestreamModel
     *
     * @param tableName 表名
     * @return IotValuestreamModel
     */
    IotValuestreamModel findTableAndColumnByTableName(String tableName);

    /**
     * 根据id查询ValuestreamModel
     *
     * @param id 主键id
     * @return IotValuestreamModel
     */
    IotValuestreamModel findById(Integer id);

    /**
     * 更新ValuestreamModel
     *
     * @param valuestreamModel
     * @return int
     */
    int updateTable(@Param("valuestreamModel") IotValuestreamModel valuestreamModel);

    /**
     * 删除valuestreamModel
     * @param valuestreamModel
     * @return
     */
    int deleteTable(@Param("valuestreamModel") IotValuestreamModel valuestreamModel);


//    /**
//     * 根据表名和列名
//     *
//     * @param tableName 表名
//     * @param column    列名
//     * @return IotValuestreamModel1
//     */
//    IotValuestreamModel1 findExistColumnByTableNameAndColumn(@Param("name") String tableName, @Param("column") String column);
//
//    /**
//     * 更新修改信息
//     *
//     * @param existValueStreamModelIot
//     */
//    void updateConfigurationChanges(@Param("existValueStreamModelIot") IotValuestreamModel1 existValueStreamModelIot);

}