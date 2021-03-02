package com.pisx.emqxbackendpostgresql.mapper;

import com.pisx.emqxbackendpostgresql.entity.MqttMsg;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author boliu
 * @since 2020-10-21
 */
@Mapper
public interface MqttMsgMapper extends BaseMapper<MqttMsg> {

}
