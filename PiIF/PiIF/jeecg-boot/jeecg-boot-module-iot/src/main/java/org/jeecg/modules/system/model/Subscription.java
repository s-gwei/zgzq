package org.jeecg.modules.system.model;

import lombok.Data;

/**
 * @author zhang ho jian
 * @date 2020/9/15
 * @time 9:22
 * @Description :订阅定义模型
 */
@Data
public class Subscription {

    //名称
    private String name;
    //唯一标识
    private String  SubscriptionName;
    //描述
    private String description;
    //事件唯一标识
    private String eventName;
    //事件名称
    private String incident;
    //排序
    private Integer ordinal;
    //是否开启(0关闭，1启动)
    private String whetherStart;
    //服务
    private Subscriptionerve subscriptionerve;


}
