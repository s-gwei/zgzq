package org.jeecg.modules.investigation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.jeecg.modules.investigation.entity.InvestigationContainer;

/**
 * @author xduan
 * @version 2020/4/24
 * @Desc 活动表的数据操作接口
 */
@Mapper
public interface ContainerMapper extends BaseMapper<InvestigationContainer> {
}
