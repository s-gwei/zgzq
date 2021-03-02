package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.jeecg.modules.system.entity.ServeInformation;

/**
 * @author zhang ho jian
 * @date 2020/9/25
 * @time 15:53
 * @Description : 查询可订阅的服务mapper
 */
@Mapper
public interface ServeInformationMapper extends BaseMapper<ServeInformation> {

}
