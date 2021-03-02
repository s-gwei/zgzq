package org.jeecg.modules.stream.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.stream.entity.IotStreamModel;
import org.jeecg.modules.stream.entity.StreamModelVO;

/**
 * @Author:wzp
 * @Date:Created in 9:45 AM 2020/3/20
 * @Description:IStreamModelService
 */
public interface IStreamModelService extends IService<IotStreamModel> {


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
    IPage<IotStreamModel> query(Page<IotStreamModel> page, String name, String description, String tags, String projectName, String userId);


    /**
     * 新增streamModel
     *
     * @param streamModelVO
     * @return Result
     */
    Result<Object> add(StreamModelVO streamModelVO);

    /**
     * 更新streamModel
     *
     * @param streamModelVO
     * @return Result
     */
    Result<Object> updateStreamModel(StreamModelVO streamModelVO);

    /**
     * 删除streamModel
     *
     * @param tableName 表名
     * @param user      删除人
     * @return Result
     */
    Result<Object> deleteStreamModel(String tableName, String user);

    /**
     * 查询混搭信息
     *
     * @param pageNo
     * @param pageSize
     * @param tableName
     * @return Result
     */
    Result<Object> findMashUpData(Integer pageNo, Integer pageSize, String tableName);

    /**
     * 查询StreamModel
     *
     * @param name stream表名
     * @return Result
     */
    Result<Object> findStreamById(String name);
}
