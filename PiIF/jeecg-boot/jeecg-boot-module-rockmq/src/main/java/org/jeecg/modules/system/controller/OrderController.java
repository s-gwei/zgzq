package org.jeecg.modules.system.controller;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.jeecg.modules.system.jms.MsgProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

/**
 * @Author:wzp
 * @Date:Created in 10:33 AM 2020/2/20
 * @Description:
 */
@RestController
@RequestMapping("/api/v1")
public class OrderController {

    @Autowired
    private MsgProducer msgProducer;

    @GetMapping("/send")
    public Object order(String msg, String tag) throws UnsupportedEncodingException,
            InterruptedException, RemotingException, MQClientException, MQBrokerException {
        //创建一个消息实例，包含 topic、tag 和 消息体
        //如下：topic 为 "TopicTest"，tag 为 "push"
        Message message = new Message("TopicTest", tag, msg.getBytes(RemotingHelper.DEFAULT_CHARSET));

        SendResult result = msgProducer.getProducer().send(message);

        System.out.println("消息内容:" + msg + "  MSG ID:" + result.getMsgId() + "," + "发送状态:" + result.getSendStatus());

        return "success";

    }
}
