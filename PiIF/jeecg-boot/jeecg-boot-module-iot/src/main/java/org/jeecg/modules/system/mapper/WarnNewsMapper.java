package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.jeecg.modules.system.entity.SysUserRole;
import org.jeecg.modules.system.entity.WarnNews;

/**
 * @author zhang ho jian
 * @date 2020/9/8
 * @time 15:14
 * @Description :查询警告消息map
 */
@Mapper
public interface WarnNewsMapper extends BaseMapper<WarnNews> {
}
