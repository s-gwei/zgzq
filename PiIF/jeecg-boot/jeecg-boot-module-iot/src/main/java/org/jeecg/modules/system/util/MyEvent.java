package org.jeecg.modules.system.util;


import lombok.Data;
import org.jeecg.modules.system.entity.Thing;
import org.jeecg.modules.system.model.PropertyDefinition;
import org.jeecg.modules.system.model.Subscription;
import org.springframework.context.ApplicationEvent;

import java.util.Map;

/**
 * @author zhang ho jian
 * @date 2020/8/31
 * @time 13:43
 * @Description :自定义监听方法测试类
 */
public class MyEvent extends ApplicationEvent {

    private String eventName;

    private Map<String, Subscription> subscriptions;


    /**
     * 在自定义事件的构造方法中除了第一个source参数，其他参数都可以去自定义，
     * 可以根据项目实际情况进行监听传参，这里就只定义个简单的String字符串的透传
     * @param source
     *
     */
    public MyEvent(Object source, Map<String, Subscription> subscriptions , String eventName) {
        super(source);
        this.subscriptions = subscriptions;
        this.eventName = eventName;
    }

    /**
     * 自定义监听器触发的透传打印方法
     * @param msg
     */
    public void printMsg(String msg)
    {
        System.out.println("编程事件监听:" + msg);
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Map<String, Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Map<String, Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }
}
