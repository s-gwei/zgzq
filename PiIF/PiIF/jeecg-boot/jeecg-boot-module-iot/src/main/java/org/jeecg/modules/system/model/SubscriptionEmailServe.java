package org.jeecg.modules.system.model;

import lombok.Data;

/**
 * @author zhang ho jian
 * @date 2020/9/27
 * @time 9:46
 * @Description :订阅邮件服务
 */
@Data
public class SubscriptionEmailServe {

    //发件人邮箱
    private String sendEmail;
    //收件人邮箱
    private String addresseeEmail;
    //邮箱主题
    private String emailTheme;
    //邮箱内容
    private String emailContent;
    //邮件服务器的主机名
    private String mailServer;
    //登录邮箱授权码
    private String loginAuthCode;


}
