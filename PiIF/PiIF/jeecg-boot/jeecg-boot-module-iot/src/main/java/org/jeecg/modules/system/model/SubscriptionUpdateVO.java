package org.jeecg.modules.system.model;

import lombok.Data;

import java.util.Map;

/**
 * @author zhang ho jian
 * @date 2020/9/29
 * @time 17:39
 * @Description :更新订阅的vo
 */
@Data
public class SubscriptionUpdateVO {
    private String SubscriptionName;
    private  String thingName;
    private  String type;
    private Map<String ,String> data;
    private String  whetherStart;
}
