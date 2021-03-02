package org.jeecg.modules.system.controller;

import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.system.Incident.Incident;
import org.jeecg.modules.system.entity.ServeInformation;
import org.jeecg.modules.system.entity.ThingTemplate;
import org.jeecg.modules.system.entity.WarnNews;
import org.jeecg.modules.system.model.*;
import org.jeecg.modules.system.service.IncidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author zhang ho jian
 * @date 2020/8/31
 * @time 14:35
 * @Description :事件controller
 */
@RestController
@RequestMapping("/incident")
@Api("事件")
@Slf4j
public class IncidentController {

    @Autowired
    private IncidentService incidentService;

    @RequestMapping(value = "/getEmailServe", method = RequestMethod.POST)
    @ApiOperation("短信服务提示")
    public Map<String, Object> getEmailServe() {
        HashMap<String, Object> map = new HashMap<>();
        String[] filedName = getFiledName(new SubscriptionNoteServe());
        map.put("SubscriptionNoteServe", filedName);
        return map;
    }

    @RequestMapping(value = "/getNoteServe", method = RequestMethod.POST)
    @ApiOperation("邮箱服务提示")
    public Map<String, Object> getNoteServe() {
        HashMap<String, Object> map = new HashMap<>();
        String[] filedName = getFiledName(new SubscriptionEmailServe());
        map.put("SubscriptionEmailServe", filedName);
        return map;
    }

    /*
     * @Description:新增事件
     * @Author: zhj
     * @Date: 2020/9/1 9:18
     */
    @RequestMapping(value = "/addIncident", method = RequestMethod.POST)
    @ApiOperation("新增设备事件")
    public Result<?> addIncident(@RequestBody List<EventDefinition> eventDefinitionList, String thingName) {

        Result<?> result = new Result<>();
        //健壮性检验
        if (thingName.isEmpty()) {
            return result.success("缺少重要字段thingName模块名称");
        }
        try {
            //新增事件操作
            incidentService.addIncident(eventDefinitionList, thingName);
            result.success("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.error500(e.getMessage());
        }
        return result;
    }

    /*
     * @Description: 查询事件根据设备名称与属性名
     * @Author: zhj
     * @Date: 2020/9/7 13:56
     */
    @RequestMapping(value = "/listPageThing", method = RequestMethod.GET)
    @ApiOperation("查询设备属性事件")
    public Result<List<EventDefinition>> listPageThing(String thingName, String PropertyName) {
        Result<List<EventDefinition>> result = new Result<List<EventDefinition>>();
        try {
            //健壮性检验
            if (thingName.isEmpty()) {
                throw new Exception("参数'thingName': 不能为空");
            }
            if (PropertyName.isEmpty()) {
                throw new Exception("参数'PropertyName': 不能为空");
            }
            List<EventDefinition> eventDefinitions = incidentService.listPageThing(PropertyName, thingName);
            result.setResult(eventDefinitions);
            result.setSuccess(true);
            result.setCode(CommonConstant.SC_OK_200);
        } catch (Exception e) {
            e.printStackTrace();
            result.error500(e.getMessage());
        }
        return result;

    }

    /*
     * @Description: 查询事件根据模板名称与属性名
     * @Author: zhj
     * @Date: 2020/9/7 13:56
     */
    @RequestMapping(value = "/listPageThingTemplate", method = RequestMethod.GET)
    @ApiOperation("查询模板属性事件")
    public Result<List<EventDefinition>> listPageThingTemplate(String thingName, String PropertyName) {
        Result<List<EventDefinition>> result = new Result<List<EventDefinition>>();
        try {
            //健壮性检验
            if (thingName.isEmpty()) {
                throw new Exception("参数'thingName': 不能为空");
            }
            if (PropertyName.isEmpty()) {
                throw new Exception("参数'PropertyName': 不能为空");
            }
            List<EventDefinition> eventDefinitions = incidentService.listPageThingTemplate(PropertyName, thingName);
            result.setResult(eventDefinitions);
            result.setSuccess(true);
            result.setCode(CommonConstant.SC_OK_200);
        } catch (Exception e) {
            e.printStackTrace();
            result.error500(e.getMessage());
        }
        return result;
    }

    /*
     * @Description:取消设备事件绑定
     * @Author: zhj
     * @Date: 2020/9/9 11:45
     */
    @RequestMapping(value = "/cancelIncident", method = RequestMethod.DELETE)
    @ApiOperation("取消设备事件绑定")
    public Result<?> cancelIncident(String serialNumber, String thingName) {

        Result<?> result = new Result<>();
        //健壮性检验
        if (thingName.isEmpty()) {
            return result.success("缺少重要字段thingName模块名称");
        }
        if (serialNumber.isEmpty()) {
            return result.success("缺少重要字段serialNumber事件名");
        }
        try {
            String[] split = serialNumber.split(",");
            List<String> incidentNameList = new ArrayList<String>(Arrays.asList(split));
            incidentService.cancelIncident(incidentNameList, thingName);
            result.success("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.error500(e.getMessage());
        }
        return result;
    }

    /*
     * @Description:新增模板事件
     * @Author: zhj
     * @Date: 2020/9/9 11:45
     */
    @RequestMapping(value = "/addThingTemplateIncident", method = RequestMethod.POST)
    @ApiOperation("新增模板事件")
    public Result<?> addThingTemplateIncident(@RequestBody List<EventDefinition> eventDefinition, String thingTemplateName) {

        Result<?> result = new Result<>();
        //健壮性检验
        if (thingTemplateName.isEmpty()) {
            return result.success("缺少重要字段thingTemplateName模块名称");
        }

        try {
            //新增事件操作
            incidentService.addThingTemplateIncident(eventDefinition, thingTemplateName);
            result.success("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.error500(e.getMessage());
        }
        return result;
    }

    /*
     * @Description:取消模板事件绑定
     * @Author: zhj
     * @Date: 2020/9/9 11:45
     */
    @RequestMapping(value = "/cancelThingTemplateIncident", method = RequestMethod.DELETE)
    @ApiOperation("取消模板事件绑定")
    public Result<?> cancelThingTemplateIncident(String serialNumber, String thingName) {

        Result<?> result = new Result<>();
        //健壮性检验
        if (thingName.isEmpty()) {
            return result.success("缺少重要字段thingName模块名称");
        }
        if (serialNumber.isEmpty()) {
            return result.success("缺少重要字段serialNumber事件名");
        }
        try {
            String[] split = serialNumber.split(",");
            List<String> incidentNameList = new ArrayList<String>(Arrays.asList(split));
            incidentService.cancelThingTemplateIncident(incidentNameList, thingName);
            result.success("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.error500(e.getMessage());
        }
        return result;
    }


    /*
     * @Description:查询警告消息
     * @Author: zhj
     * @Date: 2020/9/9 11:45
     */
    @RequestMapping(value = "/warnNewsList", method = RequestMethod.GET)
    @ApiOperation("查询警告消息")
    public Result<IPage<WarnNews>> warnNewsList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                String state, String sort, String createTime, String createReachTime, String confirmTime, String confirmReachTime) {

        Result<IPage<WarnNews>> result = new Result<IPage<WarnNews>>();
        try {
            Page page = new Page(pageNo, pageSize);
            IPage<WarnNews> warnNewsIPage = incidentService.warnNewsList(page, state, sort, createTime, createReachTime, confirmTime, confirmReachTime);
            result.setResult(warnNewsIPage);
            result.setSuccess(true);
            result.setCode(CommonConstant.SC_OK_200);
        } catch (Exception e) {
            log.error(e.toString());
            result.error500(e.getMessage());
        }
        return result;
    }

    /*
     * @Description:确认警告信息
     * @Author: zhj
     * @Date: 2020/9/9 11:45
     */
    @RequestMapping(value = "/alterWarnState", method = RequestMethod.GET)
    @ApiOperation("确认警告信息")
    public Result<?> alterWarnState(String id) {
        Result<?> result = new Result<>();
        //健壮性检验
        if (id.isEmpty()) {
            return result.success("缺少重要字段id");
        }
        try {
            incidentService.alterWarnState(id);
            result.success("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.error500(e.getMessage());
        }
        return result;

    }

/*    *//*
     * @Description:订阅邮件服务
     * @Author: zhj
     * @Date: 2020/9/15 10:12
     *//*
    @RequestMapping(value = "/addEmailSubscription", method = RequestMethod.POST)
    @ApiOperation("订阅邮件服务")
    public Result<?> addEmailSubscription(@RequestBody SubscriptionEmailServe subscriptionEmailServe,
                                          String thingName, String serialNumber,String whetherStart,String name,
                                          String description,String incident) {
        Result<?> result = new Result<>();
        //健壮性校验
        if (thingName.isEmpty()) {
            return result.success("重要字段thingName设备名称不能为空");
        }
        if (serialNumber.isEmpty()) {
            return result.success("重要字段serialNumber事件名称不能为空");
        }
        if (whetherStart.isEmpty()) {
            return result.success("重要字段whetherStart启用状态不能为空");
        }
        if (name.isEmpty()) {
            return result.success("重要字段name订阅名称不能为空");
        }
        try {
            incidentService.addEmailSubscription(subscriptionEmailServe, thingName, serialNumber,whetherStart,name,description,incident);
            result.success("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.error500(e.getMessage());
        }

        return result;
    }*/

/*    @RequestMapping(value = "/addSubscription", method = RequestMethod.POST)
    @ApiOperation("订阅短信服务")
    public Result<?> addSubscription(@RequestBody SubscriptionNoteServe subscriptionNoteServe,
                                                  String thingName, String serialNumber ,String whetherStart,String name,
                                                  String description,String incident) {
        Result<?> result = new Result<>();
        //健壮性校验
        if (thingName.isEmpty()) {
            return result.success("重要字段thingName设备名称不能为空");
        }
        if (serialNumber.isEmpty()) {
            return result.success("重要字段serialNumber事件名称不能为空");
        }
        if (whetherStart.isEmpty()) {
            return result.success("重要字段whetherStart启用状态不能为空");
        }
        if (name.isEmpty()) {
            return result.success("重要字段name订阅名称不能为空");
        }
        try {
            incidentService.addSubscription(subscriptionNoteServe, thingName, serialNumber,whetherStart,name,description,incident);
            result.success("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.error500(e.getMessage());
        }

        return result;
    }*/

    /*
     * @Description:订阅服务
     * @Author: zhj
     * @Date: 2020/9/15 10:12
     */
    @RequestMapping(value = "/addSubscription", method = RequestMethod.POST)
    @ApiOperation("订阅服务")
    public Result<?> addSubscription(@RequestBody Subscription subscription,
                                          String thingName, String serialNumber) {
        Result<?> result = new Result<>();
        //健壮性校验
        if (thingName.isEmpty()) {
            return result.success("重要字段thingName设备名称不能为空");
        }
        if (serialNumber.isEmpty()) {
            return result.success("重要字段serialNumber事件名称不能为空");
        }
        if (subscription.getWhetherStart().isEmpty()) {
            return result.success("重要字段whetherStart启用状态不能为空");
        }
        if (subscription.getName().isEmpty()) {
            return result.success("重要字段name订阅名称不能为空");
        }
        try {
            incidentService.addSubscription(subscription,thingName, serialNumber);
            result.success("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.error500(e.getMessage());
        }

        return result;
    }

    @RequestMapping(value = "updateSubscription" ,method = RequestMethod.POST)
    @ApiOperation("更新订阅服务")
    public Result<?>  updateSubscription(@RequestBody SubscriptionUpdateVO subscriptionUpdateVO){
        Result<?> result = new Result<>();
        try {
            incidentService.updateSubscription(subscriptionUpdateVO);
            result.success("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.error500(e.getMessage());
        }

        return result;
    }

    @RequestMapping(value = "deleteSubscription" , method = RequestMethod.DELETE)
    @ApiOperation("删除订阅服务")
    public Result<?> deleteSubscription( String subscriptionName ,String thingName){
        Result<?> result = new Result<>();

        try {
            String[] split = subscriptionName.split("/");
            List<String> subscriptionNameList = new ArrayList<String>(Arrays.asList(split));
            incidentService.deleteSubscription(subscriptionNameList,thingName);
            result.success("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.error500(e.getMessage());

        }
        return result;
    }
    /*
     * @Description:查询订阅
     * @Author: zhj
     * @Date: 2020/9/25 9:09
     */
    @RequestMapping(value = "/listSubscription", method = RequestMethod.GET)
    @ApiOperation("查询订阅")
    public Result<List<Subscription>> listSubscription(
            String thingName, String serialNumber) {
        Result<List<Subscription>> result = new Result<>();

        try {
            //调用service查询
            List<Subscription> subscriptionIPage = incidentService.listSubscription(thingName, serialNumber);
            //返回数据信息
            result.setResult(subscriptionIPage);
            result.setSuccess(true);
            result.setCode(CommonConstant.SC_OK_200);
        } catch (Exception e) {
            log.error(e.toString());
            result.error500(e.getMessage());
        }

        return result;
    }


    /*
     * @Description:查询可订阅的服务
     * @Author: zhj
     * @Date: 2020/9/25 15:51
     */
    @RequestMapping(value = "listServeInformation", method = RequestMethod.GET)
    @ApiOperation("查询可订阅的服务")
    public Result<IPage<ServeInformation>> listServeInformation(
                                                                String hint) {

        Result<IPage<ServeInformation>> result = new Result<>();

        try {
            Page page = new Page();
            //调用service查询
            IPage<ServeInformation> informationIPage = incidentService.listServeInformation(page, hint);
            //返回数据封装
            result.setResult(informationIPage);
            result.setSuccess(true);
            result.setCode(CommonConstant.SC_OK_200);
        } catch (Exception e) {
            log.error(e.toString());
            result.error500(e.getMessage());
        }

        return result;
    }

    private static String[] getFiledName(Object o) {
        Field[] fields = o.getClass().getDeclaredFields();
        String[] fieldNames = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
//            System.out.println(fields[i].getType());
            fieldNames[i] = fields[i].getName();
        }
        return fieldNames;

    }

    /*
     * @Description: 邮件服务
     * @Author: zhj
     * @Date: 2020/10/9 14:47
     */
    @RequestMapping(value = "SubscriptionEmailServe" , method = RequestMethod.POST)
    @ApiOperation("邮件服务")
    public Result<?> SubscriptionEmailServe (SubscriptionEmailServe subscriptionEmailServe){
        Result<?> result = new Result<>();

        try {
            incidentService.SubscriptionEmailServe(subscriptionEmailServe);
            result.success("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.error500(e.getMessage());
        }

        return result;
    }


    /*
     * @Description:短信服务
     * @Author: zhj
     * @Date: 2020/10/9 14:48
     */
    @RequestMapping(value = "SubscriptionNoteServe", method = RequestMethod.POST)
    @ApiOperation("短信服务")
    public Result<?> SubscriptionNoteServe(SubscriptionNoteServe subscriptionNoteServe)  {
        Result<?> result = new Result<>();

        try {
            incidentService.SubscriptionNoteServe(subscriptionNoteServe);
            result.success("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.error500(e.getMessage());

        }
        return result;
    }
}