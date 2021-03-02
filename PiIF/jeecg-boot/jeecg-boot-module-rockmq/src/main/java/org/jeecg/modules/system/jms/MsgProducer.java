package org.jeecg.modules.system.jms;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Author:wzp
 * @Date:Created in 10:33 AM 2020/2/20
 * @Description:
 */
@Component
public class MsgProducer {
    /**
     * 生产者的组名
     */
    @Value("${apache.rocketmq.producer.producerGroup}")
    private String producerGroup;

    /**
     * NameServer 地址
     */
    @Value("${apache.rocketmq.namesrvAddr}")
    private String namesrvAddr;

    private DefaultMQProducer producer;

    public DefaultMQProducer getProducer() {
        return producer;
    }

    @PostConstruct //@PostContruct是spring框架的注解，在方法上加该注解会在项目启动的时候执行该方法，也可以理解为在spring容器初始化的时候执行该方法。
    public void defaultMQProducer() {
        //生产者的组名
        producer = new DefaultMQProducer(producerGroup);
        //指定NameServer地址，多个地址以 ; 隔开
        producer.setNamesrvAddr(namesrvAddr);
        producer.setVipChannelEnabled(false);
        try {
            /**
             * Producer对象在使用之前必须要调用start初始化，初始化一次即可
             * 注意：切记不可以在每次发送消息时，都调用start方法
             */
            producer.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
//         finally {
        //一般应用在上下文,关闭的时候使用,通常在监听器中用
//             producer.shutdown();
//         }
    }

}