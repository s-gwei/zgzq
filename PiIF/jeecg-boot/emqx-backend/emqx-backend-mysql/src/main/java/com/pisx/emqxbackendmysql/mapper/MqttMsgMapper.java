package com.pisx.emqxbackendmysql.mapper;

import com.pisx.emqxbackendmysql.entity.MqttMsg;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author boliu
 * @since 2020-10-20
 */
@Mapper
public interface MqttMsgMapper extends BaseMapper<MqttMsg> {

}
