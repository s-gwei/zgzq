package org.jeecg.modules.system.model;

import lombok.Data;

/**
 * @author zhang ho jian
 * @date 2020/10/9
 * @time 13:42
 * @Description :服务对象
 */
@Data
public class Subscriptionerve {
    //短信服务
    private SubscriptionNoteServe subscriptionNoteServe;
    //邮件服务
    private SubscriptionEmailServe subscriptionEmailServe;



}
