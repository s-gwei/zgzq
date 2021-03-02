package com.pisx.emqxbackendmysql.service.impl;

import com.pisx.emqxbackendmysql.entity.MqttMsg;
import com.pisx.emqxbackendmysql.mapper.MqttMsgMapper;
import com.pisx.emqxbackendmysql.service.IMqttMsgService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author boliu
 * @since 2020-10-20
 */
@Service
public class MqttMsgServiceImpl extends ServiceImpl<MqttMsgMapper, MqttMsg> implements IMqttMsgService {

}
