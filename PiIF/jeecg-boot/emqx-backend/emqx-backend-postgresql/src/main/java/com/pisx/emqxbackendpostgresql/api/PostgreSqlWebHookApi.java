package com.pisx.emqxbackendpostgresql.api;

import cn.hutool.core.bean.BeanUtil;
import com.pisx.emqxbackendpostgresql.constant.WebHookConstant;
import com.pisx.emqxbackendpostgresql.entity.MqttMsg;
import com.pisx.emqxbackendpostgresql.service.IMqttMsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Description: postgresql接收emqx消息api
 * @author: boliu
 * @date: 2020年10月12日 8:54
 */
@RestController
@RequestMapping("/api/webHook")
@Slf4j
public class PostgreSqlWebHookApi {

    @Autowired
    private IMqttMsgService mqttMsgService;

    @PostMapping("/postgresql")
    public void receiveWebHookData(@RequestBody Map<String, Object> param) {
        String topic = (String) param.get("topic");
        String action = (String) param.get("action");
        if (WebHookConstant.MESSAGE_PUBLISH.equals(action)) {
            if (topic.startsWith(WebHookConstant.TOPIC)) {
                MqttMsg mqttMsg = BeanUtil.fillBeanWithMap(param, new MqttMsg(), false);
                mqttMsgService.save(mqttMsg);
            }
        }
    }

}
