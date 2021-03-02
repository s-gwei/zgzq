package com.pisx.emqxbackendpostgresql.service.impl;

import com.pisx.emqxbackendpostgresql.entity.MqttMsg;
import com.pisx.emqxbackendpostgresql.mapper.MqttMsgMapper;
import com.pisx.emqxbackendpostgresql.service.IMqttMsgService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author boliu
 * @since 2020-10-21
 */
@Service
public class MqttMsgServiceImpl extends ServiceImpl<MqttMsgMapper, MqttMsg> implements IMqttMsgService {

}
