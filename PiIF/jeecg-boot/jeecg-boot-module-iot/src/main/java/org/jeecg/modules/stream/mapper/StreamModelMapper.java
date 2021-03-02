package org.jeecg.modules.stream.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.stream.entity.IotStreamModel;

/**
 * @Author:wzp
 * @Date:Created in 9:45 AM 2020/3/20
 * @Description:StreamModelMapper
 */
public interface StreamModelMapper extends BaseMapper<IotStreamModel> {

    /**
     * 根据StreamModel的name查询stream
     *
     * @param name 名称
     * @return IotStreamModel
     */
    IotStreamModel findByStreamName(String name);

    /**
     * 更新iotStreamModel
     *
     * @param iotStreamModel
     * @return int
     */
    int updateStreamById(@Param("iotStreamModel") IotStreamModel iotStreamModel);


    /**
     * 根据id删除streamModel
     *
     * @param streamModel
     * @return int
     */
    int deleteStreamModel(@Param("streamModel") IotStreamModel streamModel);

}