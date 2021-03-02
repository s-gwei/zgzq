package org.jeecg.modules.system.jms;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @Author:wzp
 * @Date:Created in 10:33 AM 2020/2/20
 * @Description:
 */
@Component
public class MsgConsumer {
    /**
     * 消费者的组名
     */
    @Value("${apache.rocketmq.consumer.PushConsumer}")
    private String consumerGroup;

    /**
     * NameServer 地址
     */
    @Value("${apache.rocketmq.namesrvAddr}")
    private String namesrvAddr;


    @PostConstruct //@PostContruct是spring框架的注解，在方法上加该注解会在项目启动的时候执行该方法，也可以理解为在spring容器初始化的时候执行该方法。
    public void defaultMQConsumer() {
        //消费者的组名
        DefaultMQPushConsumer pushConsumer = new DefaultMQPushConsumer(consumerGroup);
        //指定NameServer地址，多个地址以 ; 隔开
        pushConsumer.setNamesrvAddr(namesrvAddr);
        try {
            //订阅端，分类
            pushConsumer.subscribe("TopicTest", "123");
            //默认策略，从队尾开始消费，跳过历史消息
            pushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            //注册监听
            pushConsumer.registerMessageListener(new MessageListenerConcurrently() {//无序，并行处理  效率高
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext content) {
                    try {
                        System.out.println("消费者成功消费" + "\n");
                        for (MessageExt messageExt : list) {
                            System.out.println("messageExt:" + messageExt);
                            String s = new String(messageExt.getBody(), RemotingHelper.DEFAULT_CHARSET);
                            System.out.println("MsgID:" + messageExt.getMsgId() + "," + "消息内容:" + s);
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
            pushConsumer.start();
        } catch (
                Exception e) {
            e.printStackTrace();
        }
//         finally {
        //一般应用在上下文,关闭的时候使用,通常在监听器中用
//             producer.shutdown();
//         }
    }

}