package org.jeecg.modules.system.service.impl;

import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.util.DySmsEnum;
import org.jeecg.common.util.DySmsHelper;
import org.jeecg.common.util.TokenUtils;
import org.jeecg.modules.system.entity.ServeInformation;
import org.jeecg.modules.system.entity.Thing;
import org.jeecg.modules.system.entity.ThingTemplate;
import org.jeecg.modules.system.entity.WarnNews;
import org.jeecg.modules.system.enumerate.*;
import org.jeecg.modules.system.mapper.ServeInformationMapper;
import org.jeecg.modules.system.mapper.ThingMapper;
import org.jeecg.modules.system.mapper.ThingTemplateMapper;
import org.jeecg.modules.system.mapper.WarnNewsMapper;
import org.jeecg.modules.system.model.*;
import org.jeecg.modules.system.service.IncidentService;
import org.jeecg.modules.system.util.EmailUtil;
import org.jeecg.modules.system.util.MqttMessageUtil;
import org.jeecg.modules.system.util.MyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author zhang ho jian
 * @date 2020/8/31
 * @time 13:52
 * @Description :事件
 */
@Slf4j
@Service
public class IncidentServieImpl implements IncidentService {



    @Resource
    private ThingTemplateMapper thingTemplateMapper;

    @Resource
    private ThingMapper thingMapper;

    @Resource
    private WarnNewsMapper warnNewsMapper;

    @Resource
    private ServeInformationMapper serveInformationMapper;


    /*
     * @Description:根据属性名绑定事件
     * @Author: zhj
     * @Date: 2020/8/31 17:16
     */
    @Override
    public void addIncident(List<EventDefinition> eventDefinitionList, String thingName) {
        //查询模板
        Thing thing = thingMapper.selectById(thingName);

        //设置修改信息
        setUpdateInfo(thing);
        //获取扩展字段
        String thingShapeStr = thing.getThingShape();
        ThingShape thingShape = JSON.parseObject(thingShapeStr, ThingShape.class);
        Map<String, EventDefinition> eventDefinitions = thingShape.getEventDefinitions();
        eventDefinitionList.forEach( a ->{
            //属性名+修改的值+规则属性做为唯一标识
            a.setSerialNumber(a.getPropertyName()+a.getOperatorSchema()+a.getRule());
            int size = eventDefinitions.size();
            //设置顺序
            a.setOrdinal(size + 1);
            //添加事件
            eventDefinitions.put(a.getSerialNumber(), a);
        });

        thingShapeStr = JSON.toJSONString(thingShape);
        thing.setThingShape(thingShapeStr);
        //保存修改
        thingMapper.updateById(thing);
    }



    /*
     * @Description: 解除设备事件绑定
     * @Author: zhj
     * @Date: 2020/9/2 16:16
     */
    @Override
    public void cancelIncident(List<String> incidentNameList, String thingName) {
        //查询模板
        Thing thing = thingMapper.selectById(thingName);
        setUpdateInfo(thing);
        ThingShape thingShape = getThingShape(thing);
        Map<String, EventDefinition> eventDefinitions = thingShape.getEventDefinitions();
        for (String incidentName : incidentNameList) {
            //移除事件
            eventDefinitions.remove(incidentName);
        }
        thing.setThingShape(JSON.toJSONString(thingShape));
        thingMapper.updateById(thing);
    }

    /*
     * @Description: 添加模板事件
     * @Author: zhj
     * @Date: 2020/9/9 9:57
     */
    @Override
    public void addThingTemplateIncident(List<EventDefinition> eventDefinitionList, String thingName) {
        //查询模板
        ThingTemplate thingTemplate = thingTemplateMapper.selectById(thingName);

        //设置修改信息
        setUpdateInfo(thingTemplate);
        //获取扩展字段
        String thingShapeStr = thingTemplate.getThingShape();
        ThingShape thingShape = JSON.parseObject(thingShapeStr, ThingShape.class);
        Map<String, EventDefinition> eventDefinitions = thingShape.getEventDefinitions();
        eventDefinitionList.forEach( a ->{
            //属性名+修改的值+规则的值做为唯一标识
            a.setSerialNumber(a.getPropertyName()+a.getOperatorSchema()+a.getRule());
            int size = eventDefinitions.size();
            //设置顺序
            a.setOrdinal(size + 1);
            //添加事件
            eventDefinitions.put(a.getSerialNumber(), a);
        });
        thingShapeStr = JSON.toJSONString(thingShape);
        thingTemplate.setThingShape(thingShapeStr);
        //保存修改
        thingTemplateMapper.updateById(thingTemplate);
        //给继承模板的设备新增属性
        /* editThing(thingName, eventDefinition);*/
    }


    /*
     * @Description:解除模板事件绑定
     * @Author: zhj
     * @Date: 2020/9/9 9:57
     */
    @Override
    public void cancelThingTemplateIncident(List<String> incidentNameList, String thingName) {
        //查询模板
        ThingTemplate thingTemplate = thingTemplateMapper.selectById(thingName);
        //设置修改信息
        setUpdateInfo(thingTemplate);
        //获取属性扩展字段
        String thingShapeStr = thingTemplate.getThingShape();
        ThingShape thingShape = JSON.parseObject(thingShapeStr, ThingShape.class);
        Map<String, EventDefinition> eventDefinitions = thingShape.getEventDefinitions();
        for (String Name : incidentNameList) {
            //删除属性
            eventDefinitions.remove(Name);
        }
        thingShapeStr = JSON.toJSONString(thingShape);
        thingTemplate.setThingShape(thingShapeStr);
        //保存修改
        thingTemplateMapper.updateById(thingTemplate);

        //删除继承模板实例的属性
        List<Thing> thingList = thingMapper.findThingByThingTemplate(thingName);
        for (Thing thing : thingList) {
            setUpdateInfo(thing);
            String thingShape1Str = thing.getThingShape();
            ThingShape thingShape1 = JSON.parseObject(thingShape1Str, ThingShape.class);
            Map<String, EventDefinition> eventDefinitionsThing = thingShape.getEventDefinitions();
            for (String name : incidentNameList) {
                //删除属性
                eventDefinitionsThing.remove(name);
            }
            thingShapeStr = JSON.toJSONString(thingShape1);
            thing.setThingShape(thingShapeStr);
            thingMapper.updateById(thing);
        }
    }

    /*
     * @Description:查询设备属性事件
     * @Author: zhj
     * @Date: 2020/9/7 14:06
     */
    @Override
    public  List<EventDefinition> listPageThing(String propertyName, String thingName) {
        //查询设备
        Thing thing = thingMapper.selectById(thingName);
        if (thing == null) {
            return new ArrayList<>();
        }
        //设置修改属性
        setUpdateInfo(thing);
        //获取事件扩展字段
        String thingShapeStr = thing.getThingShape();
        ThingShape thingShape = JSON.parseObject(thingShapeStr, ThingShape.class);
        Map<String, EventDefinition> eventDefinitions = thingShape.getEventDefinitions();
        Collection<EventDefinition> values = eventDefinitions.values();
        ArrayList<EventDefinition> eventDefinitionsList = new ArrayList<>(values.size());
        values.forEach(( a ->{
            if (a.getPropertyName().equals(propertyName)){
                eventDefinitionsList.add(a);
            }
        }));
        return eventDefinitionsList;
    }

    /*
     * @Description:查询模板属性事件
     * @Author: zhj
     * @Date: 2020/9/7 14:06
     */
    @Override
    public List<EventDefinition> listPageThingTemplate(String propertyName, String thingName) {
        //查询模板
        ThingTemplate thingTemplate = thingTemplateMapper.selectById(thingName);
        if (thingTemplate == null) {
            return new ArrayList<>();
        }
        //设置修改属性
        setUpdateInfo(thingTemplate);
        //获取事件扩展字段
        String thingShapeStr = thingTemplate.getThingShape();
        ThingShape thingShape = JSON.parseObject(thingShapeStr, ThingShape.class);
        Map<String, EventDefinition> eventDefinitions = thingShape.getEventDefinitions();
        Collection<EventDefinition> values = eventDefinitions.values();
        ArrayList<EventDefinition> eventDefinitionsList = new ArrayList<>(values.size());
        values.forEach(( a ->{
            if (a.getPropertyName().equals(propertyName)){
                eventDefinitionsList.add(a);
            }
        }));
        return eventDefinitionsList;
    }

    /*
     * @Description:模糊查询警告信息
     * @Author: zhj
     * @Date: 2020/9/9 11:46
     */
    @Override
    public IPage<WarnNews> warnNewsList(Page page, String state, String sort , String createTime, String createReachTime, String confirmTime, String confirmReachTime) {

        QueryWrapper<WarnNews> queryWrapper = new QueryWrapper<WarnNews>();
        if (StringUtils.hasText(state)){
            queryWrapper.like("state", state);
        }
        if (StringUtils.hasText(createTime)){
            queryWrapper.ge("create_time", createTime);
        }
        if (StringUtils.hasText(createReachTime)){
            queryWrapper.le("create_time", createReachTime);
        }
        if (StringUtils.hasText(confirmTime)){
            queryWrapper.ge("confirm_time", confirmTime);
        }
        if (StringUtils.hasText(confirmReachTime)){
            queryWrapper.le("confirm_time", confirmReachTime);
        }
        if (sort.equals("desc")){
            queryWrapper.orderByDesc("confirm_time");
            queryWrapper.orderByDesc("create_time");
        }else {
            queryWrapper.orderByAsc("confirm_time");
            queryWrapper.orderByAsc("create_time");
        }

        return warnNewsMapper.selectPage(page, queryWrapper);
    }

    /*
     * @Description: 添加警告消息
     * @Author: zhj
     * @Date: 2020/9/8 15:19
     */
    public void addWarnNews(EventDefinition eventDefinitions, String thingName){
        //健壮性校验，避免出现空记录数据
        if (!StringUtils.isEmpty(thingName) && eventDefinitions != null){
            WarnNews warnNews = new WarnNews();
            //数据封装
            warnNews.setDesciption(eventDefinitions.getDescription());//描述
            warnNews.setPropertyName(eventDefinitions.getPropertyName());//属性名
            warnNews.setIncidentName(eventDefinitions.getName());//事件名
            warnNews.setState(WarnNewsStateEnum.UNCONFIRMED.getValue());//状态
            warnNews.setThingName(thingName);//设备名称

            //保存
            warnNewsMapper.insert(warnNews);
        }

    }


    /*
     * @Description: 确定警告消息（更改状态）
     * @Author: zhj
     * @Date: 2020/9/8 15:21
     */
    public void  alterWarnState(String id){
        //根据id获取对应的警告信息
        WarnNews warnNews = warnNewsMapper.selectById(id);
        //更改警告信息状态
        warnNews.setState(WarnNewsStateEnum.CONFIRMED.getValue());
        //确认时间
        warnNews.setConfirmTime(new Date());
        //更新
        warnNewsMapper.updateById(warnNews);
    }

   /* *//*
     * @Description:订阅短信服务
     * @Author: zhj
     * @Date: 2020/9/15 9:51
     *//*
    @Override
    public void addSubscription(SubscriptionNoteServe subscriptionNoteServe, String thingName , String serialNumber,String whetherStart,String name,String description,String incident) {
        Subscription subscription = new Subscription();
        subscription.setSubscriptionNoteServe(subscriptionNoteServe);
        subscription.setWhetherStart(whetherStart);
        subscription.setName(name);
        subscription.setDescription(description);
        subscription.setIncident(incident);
        //查询设备信息
        Thing thing = thingMapper.selectById(thingName);
        //设置修改属性
        setUpdateInfo(thing);
        //获取扩展属性
        String thingShapeStr = thing.getThingShape();
        ThingShape thingShape = JSON.parseObject(thingShapeStr, ThingShape.class);
        Map<String, Subscription> subscriptions = thingShape.getSubscriptions();
        int size = subscriptions.size();
        //顺序
        subscription.setOrdinal(size+1);
        subscription.setEventName(serialNumber);//事件的唯一标识

        //唯一标识（事件的唯一标识叫电话/邮箱）
        if ( subscriptionNoteServe.getReceptionPhone()!=null && subscription.getSubscriptionName()== null){
            subscription.setSubscriptionName(serialNumber+subscriptionNoteServe.getReceptionPhone());
        }else {
            subscription.setSubscriptionName(serialNumber);
        }

        //添加订阅信息
        subscriptions.put(subscription.getSubscriptionName(),subscription);
        thingShapeStr = JSON.toJSONString(thingShape);
        thing.setThingShape(thingShapeStr);
        //保存修改
        thingMapper.updateById(thing);

    }*/


    /*
     * @Description:查询订阅
     * @Author: zhj
     * @Date: 2020/9/25 9:17
     */
    @Override
    public List<Subscription> listSubscription( String thingName, String serialNumber) {
        //获取设备信息
        Thing thing = thingMapper.selectById(thingName);
        if (thing == null) {
            return new ArrayList<>();
        }
        //获取json串中的订阅消息转换成对应的实体
        String thingShapeStr = thing.getThingShape();
        ThingShape thingShape = JSON.parseObject(thingShapeStr, ThingShape.class);
        Map<String, Subscription> subscriptions = thingShape.getSubscriptions();
        Collection<Subscription> values = subscriptions.values();
        List<Subscription> subscriptionList = new ArrayList<Subscription>(values.size());

        //返回数据的封装校验，判断事件名称为空则返回全部，不为空则返回对应事件的订阅
        if (StringUtils.isEmpty(serialNumber)) {
            subscriptions.forEach((k, v) -> {
                subscriptionList.add(v);
            });
        } else {
            subscriptions.forEach((k, v) -> {
                if (v.getEventName().equals(serialNumber)) {
                    subscriptionList.add(v);
                }
            });
        }

        return subscriptionList;
    }

    /*
     * @Description:查询可订阅的服务
     * @Author: zhj
     * @Date: 2020/9/25 15:50
     */
    @Override
    public IPage<ServeInformation> listServeInformation(Page page , String hint) {

        QueryWrapper<ServeInformation> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(hint)){
            queryWrapper.like("hint", hint);
        }
        return serveInformationMapper.selectPage(page,queryWrapper);
    }

   /* *//*
     * @Description:订阅邮件服务
     * @Author: zhj
     * @Date: 2020/9/29 9:29
     *//*
    @Override
    public void addEmailSubscription(SubscriptionEmailServe subscriptionEmailServe, String thingName, String serialNumber,String whetherStart,String name,String description,String incident) {
        Subscription subscription = new Subscription();
        subscription.setSubscriptionEmailServe(subscriptionEmailServe);
        subscription.setWhetherStart(whetherStart);
        subscription.setName(name);
        subscription.setDescription(description);
        subscription.setIncident(incident);
        //查询设备信息
        Thing thing = thingMapper.selectById(thingName);
        //设置修改属性
        setUpdateInfo(thing);
        //获取扩展属性
        String thingShapeStr = thing.getThingShape();
        ThingShape thingShape = JSON.parseObject(thingShapeStr, ThingShape.class);
        Map<String, Subscription> subscriptions = thingShape.getSubscriptions();
        int size = subscriptions.size();
        //顺序
        subscription.setOrdinal(size+1);
        subscription.setEventName(serialNumber);//事件的唯一标识

        //唯一标识（事件的唯一标识叫电话/邮箱）
        if (!subscription.getSubscriptionEmailServe().getAddresseeEmail().isEmpty() && subscription.getSubscriptionName()== null){
            subscription.setSubscriptionName(serialNumber+subscription.getSubscriptionEmailServe().getAddresseeEmail());
            //暂时修改为可配置
            subscription.getSubscriptionEmailServe().setSendEmail(EmailConfigEnum.ZHJ.getLoginAccount());//发件人邮箱
            subscription.getSubscriptionEmailServe().setMailServer(EmailConfigEnum.ZHJ.getMailServer());//服务器主机名
            subscription.getSubscriptionEmailServe().setLoginAuthCode(EmailConfigEnum.ZHJ.getLoginAuthCode());//邮箱授权码
        }else {
            subscription.setSubscriptionName(serialNumber);
        }
        //添加订阅信息
        subscriptions.put(subscription.getSubscriptionName(),subscription);
        thingShapeStr = JSON.toJSONString(thingShape);
        thing.setThingShape(thingShapeStr);
        //保存修改
        thingMapper.updateById(thing);
    }
*/

   /*
    * @Description:更新订阅
    * @Author: zhj
    * @Date: 2020/10/9 14:08
    */
    @Override
    public void updateSubscription(SubscriptionUpdateVO subscriptionUpdateVO) {
        //查询设备信息
        Thing thing = thingMapper.selectById(subscriptionUpdateVO.getThingName());
        //设置修改属性
        setUpdateInfo(thing);
        //获取扩展属性
        String thingShapeStr = thing.getThingShape();
        ThingShape thingShape = JSON.parseObject(thingShapeStr, ThingShape.class);
        Map<String, Subscription> subscriptions = thingShape.getSubscriptions();
        //若未选服务，默认更新基本信息
        if (subscriptionUpdateVO.getType().isEmpty()){
            subscriptions.forEach((k,v)->{
                v.setWhetherStart(subscriptionUpdateVO.getWhetherStart());
                subscriptions.put(k,v);
            });
        }
        //更改服务为短信或订阅服务为短信
        if ( subscriptionUpdateVO.getSubscriptionName() != null &&subscriptionUpdateVO.getType().equals(ServeTypeEnum.NOTE.getCode()) && !subscriptionUpdateVO.getType().isEmpty()){
            subscriptions.forEach((k ,v) ->{
                SubscriptionNoteServe subscriptionNoteServe = new SubscriptionNoteServe();
                v.getSubscriptionerve().setSubscriptionNoteServe(subscriptionNoteServe);
                v.getSubscriptionerve().setSubscriptionEmailServe(null);
                if (  v.getSubscriptionName().equals(subscriptionUpdateVO.getSubscriptionName())){
                    v.getSubscriptionerve().getSubscriptionNoteServe().setReceptionPhone(subscriptionUpdateVO.getData().get(ServeInputBoxEnum.NOTESERVE.getCode()));
                        v.setSubscriptionName(v.getEventName()+subscriptionUpdateVO.getData().get(ServeInputBoxEnum.NOTESERVE.getCode()));
                        v.setWhetherStart(subscriptionUpdateVO.getWhetherStart());
                    subscriptions.put(k,v);
                }
            });
        }
         //更改服务为邮件或订阅服务为订阅
        if (subscriptionUpdateVO.getSubscriptionName() !=  null && subscriptionUpdateVO.getType().equals(ServeTypeEnum.EMAIL.getCode()) && !subscriptionUpdateVO.getType().isEmpty()){
            subscriptions.forEach((k, v) ->{
                SubscriptionEmailServe subscriptionEmailServe = new SubscriptionEmailServe();
                v.getSubscriptionerve().setSubscriptionEmailServe(subscriptionEmailServe);
                v.getSubscriptionerve().setSubscriptionNoteServe(null);
                if (v.getSubscriptionName().equals(subscriptionUpdateVO.getSubscriptionName())){
                    v.getSubscriptionerve().getSubscriptionEmailServe().setAddresseeEmail(subscriptionUpdateVO.getData().get(ServeInputBoxEnum.EMAILSERVE.getCode()));
                    //暂时修改为可配置
                    v.getSubscriptionerve().getSubscriptionEmailServe().setSendEmail(EmailConfigEnum.ZHJ.getLoginAccount());//发件人邮箱
                    v.getSubscriptionerve().getSubscriptionEmailServe().setMailServer(EmailConfigEnum.ZHJ.getMailServer());//服务器主机名
                    v.getSubscriptionerve().getSubscriptionEmailServe().setLoginAuthCode(EmailConfigEnum.ZHJ.getLoginAuthCode());//邮箱授权码
                    v.setSubscriptionName(v.getEventName()+subscriptionUpdateVO.getData().get(ServeInputBoxEnum.EMAILSERVE.getCode()));
                    v.setWhetherStart(subscriptionUpdateVO.getWhetherStart());
                    subscriptions.put(k,v);
                }
            });
        }

        thingShapeStr = JSON.toJSONString(thingShape);
        thing.setThingShape(thingShapeStr);
        //保存修改
        thingMapper.updateById(thing);
    }


    /*
     * @Description:删除订阅服务
     * @Author: zhj
     * @Date: 2020/9/30 10:17
     */
    @Override
    public void deleteSubscription(List<String> subscriptionNameList, String thingName) {
        //查询设备信息
        Thing thing = thingMapper.selectById(thingName);
        //设置修改属性
        setUpdateInfo(thing);
        //获取扩展属性
        String thingShapeStr = thing.getThingShape();
        ThingShape thingShape = JSON.parseObject(thingShapeStr, ThingShape.class);
        Map<String, Subscription> subscriptions = thingShape.getSubscriptions();
        for (String subscriptionName : subscriptionNameList){
            subscriptions.remove(subscriptionName);
        }
        thingShapeStr = JSON.toJSONString(thingShape);
        thing.setThingShape(thingShapeStr);
        thingMapper.updateById(thing);
    }

    /*
     * @Description:订阅服务
     * @Author: zhj
     * @Date: 2020/10/9 14:12
     */
    @Override
    public void addSubscription(Subscription subscription, String thingName, String serialNumber) {
        //查询设备信息
        Thing thing = thingMapper.selectById(thingName);
        //设置修改属性
        setUpdateInfo(thing);
        //获取扩展属性
        String thingShapeStr = thing.getThingShape();
        ThingShape thingShape = JSON.parseObject(thingShapeStr, ThingShape.class);
        Map<String, Subscription> subscriptions = thingShape.getSubscriptions();
        int size = subscriptions.size();
        //顺序
        subscription.setOrdinal(size+1);
        subscription.setEventName(serialNumber);//事件的唯一标识
        //唯一标识（事件的唯一标识叫电话/邮箱）
        if (!subscription.getSubscriptionerve().getSubscriptionEmailServe().getAddresseeEmail().isEmpty() && subscription.getSubscriptionName()== null){
            subscription.setSubscriptionName(serialNumber+subscription.getSubscriptionerve().getSubscriptionEmailServe().getAddresseeEmail());
            //暂时修改为可配置
            subscription.getSubscriptionerve().getSubscriptionEmailServe().setSendEmail(EmailConfigEnum.ZHJ.getLoginAccount());//发件人邮箱
            subscription.getSubscriptionerve().getSubscriptionEmailServe().setMailServer(EmailConfigEnum.ZHJ.getMailServer());//服务器主机名
            subscription.getSubscriptionerve().getSubscriptionEmailServe().setLoginAuthCode(EmailConfigEnum.ZHJ.getLoginAuthCode());//邮箱授权码
        }else if ( subscription.getSubscriptionerve().getSubscriptionNoteServe().getReceptionPhone()!=null && subscription.getSubscriptionName()== null){
            subscription.setSubscriptionName(serialNumber+subscription.getSubscriptionerve().getSubscriptionNoteServe().getReceptionPhone());
        }else {
            subscription.setSubscriptionName(serialNumber);
        }
        //添加订阅信息
        subscriptions.put(subscription.getSubscriptionName(),subscription);
        thingShapeStr = JSON.toJSONString(thingShape);
        thing.setThingShape(thingShapeStr);
        //保存修改
        thingMapper.updateById(thing);
    }

    /*
     * @Description: 邮件服务
     * @Author: zhj
     * @Date: 2020/10/9 15:31
     */
    @Override
    public void SubscriptionEmailServe(SubscriptionEmailServe subscriptionEmailServe) {
        //可以多个收件人
        String[] AddresseeEmailList = subscriptionEmailServe.getAddresseeEmail().toString().split(",");
        subscriptionEmailServe.setSendEmail(EmailConfigEnum.ZHJ.getLoginAccount());//发件人邮箱
        subscriptionEmailServe.setMailServer(EmailConfigEnum.ZHJ.getMailServer());//服务器主机名
        subscriptionEmailServe.setLoginAuthCode(EmailConfigEnum.ZHJ.getLoginAuthCode());//邮箱授权码
        //发送邮件
        new EmailUtil().sendEmail(subscriptionEmailServe.getMailServer(),
                subscriptionEmailServe.getSendEmail(),
                subscriptionEmailServe.getLoginAuthCode(),
                subscriptionEmailServe.getSendEmail(),
                AddresseeEmailList, EmailTemplateEnum.WARN.getEmailTheme(),
                EmailTemplateEnum.WARN.getEmailContent());
    }

    /*
     * @Description:短信服务
     * @Author: zhj
     * @Date: 2020/10/9 15:31
     */
    @Override
    public void SubscriptionNoteServe(SubscriptionNoteServe subscriptionNoteServe){
        JSONObject obj = new JSONObject();
        try {
            obj.put("code","测试短信");
            obj.put("product","测试");
            //发送短信
         /* DySmsHelper.sendSms(subscriptionNoteServe.getReceptionPhone(), obj, DySmsEnum.FORGET_PASSWORD_TEMPLATE_CODE);*/
            DySmsHelper.sendSms(subscriptionNoteServe.getReceptionPhone(), obj, DySmsEnum.REGISTER_TEMPLATE_CODE);
        } catch (ClientException e) {
            e.printStackTrace();

        }

    }


    /**
     * 给模板设置修改信息（修改者，修改时间）
     */
    private void setUpdateInfo(Thing thing) {
        //设置修改时间
        thing.setLastModifiedDate(new Timestamp(new Date().getTime()));
        //获取http请求信息
        String username = getUsername();
        //添加修改记录
        String configurationChanges = thing.getConfigurationChanges();
        List<String> list = JSON.parseArray(configurationChanges, String.class);
        if (list == null) {
            list = new LinkedList<>();
        }
        if (list.size() < 20) {
            //小于20条修改记录
            list.add(username);
        } else {
            list.remove(0);
            list.add(username);
        }
        thing.setConfigurationChanges(JSON.toJSONString(list));
    }

    /**
     * 给模板设置修改信息（修改者，修改时间）
     */
    private void setUpdateInfo(ThingTemplate thingTemplate) {
        //设置修改时间
        thingTemplate.setLastModifiedDate(new Timestamp(new Date().getTime()));
        //获取http请求信息
        String username = getUsername();
        //添加修改记录
        String configurationChanges = thingTemplate.getConfigurationChanges();
        List<String> list = JSON.parseArray(configurationChanges, String.class);
        if (list == null) {
            list = new LinkedList<>();
        }
        if (list.size() < 20) {
            //小于20条修改记录
            list.add(username);
        } else {
            list.remove(0);
            list.add(username);
        }
        thingTemplate.setConfigurationChanges(JSON.toJSONString(list));
    }



    /**
     * 根据token获取当前用户名，如果没有则返回""字符串

     */
    private String getUsername() {
        //获取http请求信息
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        String token = TokenUtils.getTokenByRequest(request);
        String username = "";
        if (token != null) {
            //当前用户
            username = JwtUtil.getUsername(token);
        }
        return username;
    }

    /**
     * 获取对应设备的扩展字段，并且转换为 thingShape对象
     *
     * @param thing
     * @return
     */
    private ThingShape getThingShape(Thing thing) {
        String thingShapeStr = thing.getThingShape();
        return JSON.parseObject(thingShapeStr, ThingShape.class);
    }


    private void editThing(String templateName, EventDefinition eventDefinition) {
        List<Thing> thingList = thingMapper.findThingByThingTemplate(templateName);
        for (Thing thing : thingList) {
            String thingShapeStr = thing.getThingShape();
            ThingShape thingShape = JSON.parseObject(thingShapeStr, ThingShape.class);
            Map<String, EventDefinition> eventDefinitions = thingShape.getEventDefinitions();
            //修改事件
            eventDefinitions.put(eventDefinition.getName(), eventDefinition);
            thingShapeStr = JSON.toJSONString(thingShape);
            thing.setThingShape(thingShapeStr);
            thingMapper.updateById(thing);
        }
    }








}
