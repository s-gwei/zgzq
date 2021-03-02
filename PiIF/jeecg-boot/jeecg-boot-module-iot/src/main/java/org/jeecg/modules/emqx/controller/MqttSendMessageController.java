package org.jeecg.modules.emqx.controller;

import com.alibaba.fastjson.JSON;
import com.pisx.utils.DataFormatter;
import com.pisx.utils.MqttUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Description: 测试mqtt发送消息
 * @author: boliu
 * @date: 2020年09月22日 16:24
 */
@Slf4j
@RestController
@RequestMapping("/emqx")
@Api("mqtt发送消息测试")
public class MqttSendMessageController {

    @GetMapping(value = {"/sendMessageByChannel"})
    @ApiOperation("通过通道发送消息")
    public String sendMessageByChannel(String topic, String channelName) {
        String messageContent = "{\n" +
                "  \"timestamp\" : 1600756505311,\n" +
                "  \"values\" : [ {\n" +
                "    \"id\" : \"aaa.Ramp1\",\n" +
                "    \"v\" : 35,\n" +
                "    \"q\" : true,\n" +
                "    \"t\" : 1600756496041\n" +
                "  }, {\n" +
                "    \"id\" : \"bbb.Random1\",\n" +
                "    \"v\" : 34,\n" +
                "    \"q\" : true,\n" +
                "    \"t\" : 1600756496041\n" +
                "  }, {\n" +
                "    \"id\" : \"bbb.Random1\",\n" +
                "    \"v\" : 9,\n" +
                "    \"q\" : true,\n" +
                "    \"t\" : 1600756497037\n" +
                "  }, {\n" +
                "    \"id\" : \"aaa.Ramp1\",\n" +
                "    \"v\" : 67,\n" +
                "    \"q\" : true,\n" +
                "    \"t\" : 1600756497037\n" +
                "  }, {\n" +
                "    \"id\" : \"bbb.Random1\",\n" +
                "    \"v\" : -1,\n" +
                "    \"q\" : true,\n" +
                "    \"t\" : 1600756498042\n" +
                "  }, {\n" +
                "    \"id\" : \"aaa.Ramp1\",\n" +
                "    \"v\" : 99,\n" +
                "    \"q\" : true,\n" +
                "    \"t\" : 1600756498042\n" +
                "  }, {\n" +
                "    \"id\" : \"bbb.Random1\",\n" +
                "    \"v\" : -9,\n" +
                "    \"q\" : true,\n" +
                "    \"t\" : 1600756499042\n" +
                "  }, {\n" +
                "    \"id\" : \"aaa.Ramp1\",\n" +
                "    \"v\" : 63,\n" +
                "    \"q\" : true,\n" +
                "    \"t\" : 1600756499042\n" +
                "  }, {\n" +
                "    \"id\" : \"aaa.Ramp1\",\n" +
                "    \"v\" : 95,\n" +
                "    \"q\" : true,\n" +
                "    \"t\" : 1600756500035\n" +
                "  }, {\n" +
                "    \"id\" : \"bbb.Random1\",\n" +
                "    \"v\" : 47,\n" +
                "    \"q\" : true,\n" +
                "    \"t\" : 1600756500035\n" +
                "  }, {\n" +
                "    \"id\" : \"aaa.Ramp1\",\n" +
                "    \"v\" : 59,\n" +
                "    \"q\" : true,\n" +
                "    \"t\" : 1600756501036\n" +
                "  }, {\n" +
                "    \"id\" : \"bbb.Random1\",\n" +
                "    \"v\" : 7,\n" +
                "    \"q\" : true,\n" +
                "    \"t\" : 1600756501036\n" +
                "  }, {\n" +
                "    \"id\" : \"aaa.Ramp1\",\n" +
                "    \"v\" : 91,\n" +
                "    \"q\" : true,\n" +
                "    \"t\" : 1600756502036\n" +
                "  }, {\n" +
                "    \"id\" : \"bbb.Random1\",\n" +
                "    \"v\" : -12,\n" +
                "    \"q\" : true,\n" +
                "    \"t\" : 1600756502036\n" +
                "  }, {\n" +
                "    \"id\" : \"aaa.Ramp1\",\n" +
                "    \"v\" : 55,\n" +
                "    \"q\" : true,\n" +
                "    \"t\" : 1600756503039\n" +
                "  }, {\n" +
                "    \"id\" : \"bbb.Random1\",\n" +
                "    \"v\" : 37,\n" +
                "    \"q\" : true,\n" +
                "    \"t\" : 1600756503039\n" +
                "  }, {\n" +
                "    \"id\" : \"bbb.Random1\",\n" +
                "    \"v\" : -16,\n" +
                "    \"q\" : true,\n" +
                "    \"t\" : 1600756504041\n" +
                "  }, {\n" +
                "    \"id\" : \"aaa.Ramp1\",\n" +
                "    \"v\" : 87,\n" +
                "    \"q\" : true,\n" +
                "    \"t\" : 1600756504041\n" +
                "  }, {\n" +
                "    \"id\" : \"bbb.Random1\",\n" +
                "    \"v\" : 60,\n" +
                "    \"q\" : true,\n" +
                "    \"t\" : 1600756505039\n" +
                "  }, {\n" +
                "    \"id\" : \"aaa.Ramp1\",\n" +
                "    \"v\" : 51,\n" +
                "    \"q\" : true,\n" +
                "    \"t\" : 1600756505039\n" +
                "  } ]\n" +
                "}";
        //格式化设备端数据
        Map<String, Object> stringObjectMap = DataFormatter.formatData(messageContent);
        String messageContentJson = JSON.toJSONString(stringObjectMap);
        //通过mqtt发送数据
        MqttUtils.sendMessage(topic, messageContentJson, channelName);
        return messageContentJson;
    }
}
