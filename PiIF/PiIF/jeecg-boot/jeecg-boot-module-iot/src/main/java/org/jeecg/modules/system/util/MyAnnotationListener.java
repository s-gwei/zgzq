package org.jeecg.modules.system.util;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import org.jeecg.common.util.DySmsEnum;
import org.jeecg.common.util.DySmsHelper;
import org.jeecg.modules.system.enumerate.EmailTemplateEnum;
import org.jeecg.modules.system.enumerate.InitiateStateEnum;
import org.jeecg.modules.system.model.PropertyDefinition;
import org.jeecg.modules.system.model.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import retrofit2.http.PUT;

import java.util.Map;

/**
 * @author zhang ho jian
 * @date 2020/8/31
 * @time 13:50
 * @Description :注解监听器
 */
@Component
public class MyAnnotationListener {


    @EventListener
    public void listener1(MyEvent subscriptions){
        JSONObject obj = new JSONObject();

        //获取订阅信息
        Map<String, Subscription> subscription = subscriptions.getSubscriptions();
        String eventName = subscriptions.getEventName();//事件名称

        //遍历订阅信息
        subscription.forEach(( k ,v) ->{
           if (v.getEventName().equals(eventName) && v.getWhetherStart().equals(InitiateStateEnum.start.getValue())){
               if (v.getSubscriptionerve().getSubscriptionNoteServe().getReceptionPhone().isEmpty()){
                   try {
                       obj.put("code",v.getEventName()+"事件触发");
                       obj.put("product","测试");
                       //发送短信
                       DySmsHelper.sendSms(v.getSubscriptionerve().getSubscriptionNoteServe().getReceptionPhone(), obj, DySmsEnum.FORGET_PASSWORD_TEMPLATE_CODE);
                   } catch (ClientException e) {
                       e.printStackTrace();
                   }
               }
               if (v.getSubscriptionerve().getSubscriptionEmailServe().getAddresseeEmail().isEmpty()){
                   //可以多个收件人
                   String[] AddresseeEmailList = v.getSubscriptionerve().getSubscriptionEmailServe().getAddresseeEmail().toString().split(",");
                   //发送邮件
                   new EmailUtil().sendEmail(v.getSubscriptionerve().getSubscriptionEmailServe().getMailServer(),
                                             v.getSubscriptionerve().getSubscriptionEmailServe().getSendEmail(),
                                             v.getSubscriptionerve().getSubscriptionEmailServe().getLoginAuthCode(),
                                             v.getSubscriptionerve().getSubscriptionEmailServe().getSendEmail(),
                                             AddresseeEmailList, EmailTemplateEnum.WARN.getEmailTheme(),
                               v.getEventName()+EmailTemplateEnum.WARN.getEmailContent());
               }

           }
        });
    }

    @EventListener
    public void listener2(MyEvent event)
    {
/*        //调用工具类发送邮件
       new EmailUtil().sendEmail("测试","警告"+event.getPropertyDefinition().getName()+"状态被修改，请及时处理");*/
    }
}
