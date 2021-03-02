package com.pisx.emqxbackendinfluxdb.api;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.pisx.emqxbackendinfluxdb.constant.WebHookConstant;
import com.pisx.utils.InfluxDbConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: influxdb接收emqx消息api
 * @author: boliu
 * @date: 2020年10月12日 8:54
 */
@RestController
@RequestMapping("/api/webHook")
@Slf4j
public class InfluxDbWebHookApi {

    @Autowired
    private InfluxDbConnection influxDbConnection;

    @PostMapping("/influxDb")
    public void receiveWebHookData(@RequestBody Map<String, Object> param) {

        String topic = (String) param.get("topic");
        String payload = (String) param.get("payload");
        String action = (String) param.get("action");
        if (WebHookConstant.MESSAGE_PUBLISH.equals(action)) {
            if (WebHookConstant.TOPIC.equals(topic)) {
                JSONObject payloadJsonObject = JSONUtil.parseObj(payload);
                Object params = payloadJsonObject.get("params");
                JSONObject jsonObject = JSONUtil.parseObj(params);
                Object values = jsonObject.get("values");
                JSONArray payloadArray = JSONUtil.parseArray(values);
                for (int i = 0; i < payloadArray.size(); i++) {
                    String property = payloadArray.get(i).toString();
                    System.out.println(property);
                    Map fields = JSONUtil.toBean(property, Map.class);
                    Map<String, String> tags = new HashMap<>();
                    influxDbConnection.insert(topic + "/backend", tags, fields);
                }

            }
        }
    }

}
