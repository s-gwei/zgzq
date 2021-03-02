package com.pisx.emqxbackendmysql.api;

import cn.hutool.core.bean.BeanUtil;
import com.pisx.emqxbackendmysql.constant.WebHookConstant;
import com.pisx.emqxbackendmysql.entity.MqttMsg;
import com.pisx.emqxbackendmysql.mapper.MqttMsgMapper;
import com.pisx.emqxbackendmysql.service.IMqttMsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Description: mysql接收emqx消息api
 * @author: boliu
 * @date: 2020年10月12日 8:54
 */
@RestController
@RequestMapping("/api/webHook")
@Slf4j
public class MysqlWebHookApi {

    @Autowired
    private IMqttMsgService mqttMsgService;

    @PostMapping("/mysql")
    public void receiveWebHookData(@RequestBody Map<String, Object> param) {
        String topic = (String) param.get("topic");
        String action = (String) param.get("action");
        if (WebHookConstant.MESSAGE_PUBLISH.equals(action)) {
            if (WebHookConstant.TOPIC.equals(topic)) {
                MqttMsg mqttMsg = BeanUtil.fillBeanWithMap(param, new MqttMsg(), false);
                mqttMsgService.save(mqttMsg);
            }
        }
    }

}
