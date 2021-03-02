package org.jeecg.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.system.entity.ServeInformation;
import org.jeecg.modules.system.entity.WarnNews;
import org.jeecg.modules.system.model.*;

import java.util.List;
import java.util.Map;

/**
 * @author zhang ho jian
 * @date 2020/8/31
 * @time 13:52
 * @Description :事件发布
 */
public interface IncidentService {

    /**
     * 新镇事件绑定属性
     * @param
     * @param thingName
     */
    void addIncident(List<EventDefinition> eventDefinitionList, String thingName);

    /**
     * 解除绑定
     * @param incidentNameList
     * @param thingName
     */
    void cancelIncident(List<String> incidentNameList, String thingName);

    /**
     * 新增设备属性事件绑定
     * @param
     * @param thingName
     */
    void addThingTemplateIncident(List<EventDefinition> eventDefinitionList, String thingName);

    /**
     * 解除设备属性绑定
     * @param incidentNameList
     * @param thingName
     */
    void cancelThingTemplateIncident(List<String> incidentNameList, String thingName);

    /**
     * 查询设备属性事件
     * @param propertyName
     * @param thingName
     */
    List<EventDefinition> listPageThing(String propertyName, String thingName);

    /**
     * 查询模板属性事件
     * @param propertyName
     * @param thingName
     */
    List<EventDefinition> listPageThingTemplate(String propertyName, String thingName);

    /**
     * 查询警告信息
     * @param page
     * @param state
     * @param createTime
     * @param createReachTime
     * @param confirmTime
     * @param confirmReachTime
     * @return
     */
    IPage<WarnNews> warnNewsList(Page page,String state,String sort , String createTime, String createReachTime, String confirmTime, String confirmReachTime);

    /**
     * 保持警告信息
     * @param eventDefinitions
     */
    void addWarnNews(EventDefinition eventDefinitions,String thingName);

    /**
     * 确认警告信息
     * @param id
     */
    void alterWarnState(String id);


    /**
     * c查询订阅
     * @param
     * @param thingName
     * @param serialNumber
     * @return
     */
    List<Subscription> listSubscription( String thingName, String serialNumber);

    /**
     * 查询可订阅的服务
     * @param page
     * @return
     */
    IPage<ServeInformation> listServeInformation(Page page ,String hint);


    /**
     * 更新订阅
     * @param subscriptionUpdateVO
     */
    void updateSubscription(SubscriptionUpdateVO subscriptionUpdateVO);

    /**
     * 删除订阅服务
     * @param subscriptionNameList
     * @param thingName
     */
    void deleteSubscription(List<String> subscriptionNameList, String thingName);

    /**
     * 订阅服务
     * @param subscription
     * @param thingName
     * @param serialNumber
     */
    void addSubscription(Subscription subscription, String thingName, String serialNumber);

    /**
     * 邮件服务
     * @param subscriptionEmailServe
     */
    void SubscriptionEmailServe(SubscriptionEmailServe subscriptionEmailServe);

    /**
     * 短信服务
     * @param subscriptionNoteServe
     */
    void SubscriptionNoteServe(SubscriptionNoteServe subscriptionNoteServe);
}
