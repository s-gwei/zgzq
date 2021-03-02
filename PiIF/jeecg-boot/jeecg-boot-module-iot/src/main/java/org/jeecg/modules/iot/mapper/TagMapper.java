package org.jeecg.modules.iot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.jeecg.modules.iot.entity.IotTag;

/**
 * @author xduan
 * @version 2020/4/9
 * @Desc 标签数据操作接口
 */
@Mapper
public interface TagMapper extends BaseMapper<IotTag> {
}
