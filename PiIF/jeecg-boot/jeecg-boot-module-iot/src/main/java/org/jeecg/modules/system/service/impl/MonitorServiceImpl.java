package org.jeecg.modules.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.jeecg.modules.system.entity.Thing;
import org.jeecg.modules.system.enumerate.DataTypeRuleEnum;
import org.jeecg.modules.system.enumerate.InitiateStateEnum;
import org.jeecg.modules.system.mapper.ThingMapper;
import org.jeecg.modules.system.model.EventDefinition;
import org.jeecg.modules.system.model.PropertyDefinition;
import org.jeecg.modules.system.model.Subscription;
import org.jeecg.modules.system.model.ThingShape;
import org.jeecg.modules.system.service.IncidentService;
import org.jeecg.modules.system.service.MonitorService;
import org.jeecg.modules.system.util.MyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.constraints.Past;
import java.rmi.MarshalledObject;
import java.sql.Wrapper;
import java.util.Map;

/**
 * @author zhang ho jian
 * @date 2020/10/13
 * @time 17:22
 * @Description :监听设备属性
 */
@Service
public class MonitorServiceImpl  implements MonitorService {

    @Resource
    private ApplicationContext applicationContext;
    @Resource
    private ThingMapper thingMapper;
    @Resource
    private IncidentService incidentService;

    @Override
    public void monitor(Map<String, Object> map ,String headingCode) {

        //查询模板
        QueryWrapper<Thing> thingQueryWrapper = new QueryWrapper<>();
        thingQueryWrapper.lambda().eq(Thing::getIdentifier,headingCode.split("/")[1]);
        Thing thing = thingMapper.selectOne(thingQueryWrapper);
        //获取扩展字段
        String thingShapeStr = thing.getThingShape();
        ThingShape thingShape = JSON.parseObject(thingShapeStr, ThingShape.class);
        Map<String, EventDefinition> eventDefinitions = thingShape.getEventDefinitions();//事件信息
        Map<String, Subscription> subscriptions = thingShape.getSubscriptions();//订阅信息

        for (Map.Entry<String,Object> entry :map.entrySet()){
            //遍历数据
            eventDefinitions.forEach(( k,v ) ->{
                if (entry.getKey()!=null && v.getOperatorSchema()!=null && v.getPropertyName().equals(entry.getKey()) && v.getWhetherStart().equals("1") ){
                    Boolean aBoolean = ruleMatching(entry.getKey(),entry.getValue(),k, v);
                    if (aBoolean){
                        //添加警报信息
                        incidentService.addWarnNews(eventDefinitions.get(v.getSerialNumber()),thing.getName());
                        applicationContext.publishEvent(new MyEvent(this,subscriptions,v.getSerialNumber()));
                    }
                }
            });
        }
    }

    //匹配规则
    private Boolean  ruleMatching(String key ,Object value, String k , EventDefinition v){
        Boolean result = false;
        if (v.getRule()!=null){
            //达到定义的值触发警报
            if (v.getRule().equals(DataTypeRuleEnum.EQUALTO.getValue())){
                if (
                        v.getOperatorSchema() != null &&
                        v.getPropertyName().equals(key) &&
                        v.getWhetherStart().equals(InitiateStateEnum.start.getValue()) &&
                        v.getOperatorSchema().equals(value)) {
                    result = true;
                }
            }
            //未达到定义的值触发警报
            if (v.getRule().equals(DataTypeRuleEnum.NOTEQUALTO.getValue())){
                if (
                        v.getOperatorSchema() != null &&
                        v.getPropertyName().equals(key) &&
                        v.getWhetherStart().equals(InitiateStateEnum.start.getValue()) &&
                        !v.getOperatorSchema().equals(value)) {
                    result = true;
                }
            }
            //超过或者达到定义的限制(上方)
            if (v.getRule().equals(DataTypeRuleEnum.ABOVE.getValue())) {
                double OperatorSchema = Double.parseDouble( v.getOperatorSchema());
                double Value = Double.parseDouble(value.toString());
                if (
                        v.getOperatorSchema() != null &&
                        v.getPropertyName().equals(key) &&
                        v.getWhetherStart().equals(InitiateStateEnum.start.getValue()) &&
                        OperatorSchema <= Value) {
                    result = true;
                }
            }
            //低于或低于极限（下方）
            if (v.getRule().equals(DataTypeRuleEnum.UNDER.getValue())) {
                double OperatorSchema = Double.parseDouble( v.getOperatorSchema());
                double Value = Double.parseDouble(value.toString());
                if (
                        v.getOperatorSchema() != null &&
                        v.getPropertyName().equals(key) &&
                        v.getWhetherStart().equals(InitiateStateEnum.start.getValue()) &&
                        OperatorSchema >= Value) {
                    result = true;
                }
            }
            //当值在定义的范围时触发警报
            if (v.getRule().equals(DataTypeRuleEnum.INRANGE.getValue())) {
                String[] split = v.getOperatorSchema().split("\\|");
                double maximum = Double.parseDouble(split[1]);//最大
                double mauimum = Double.parseDouble(split[0]);//最小
                double Value = Double.parseDouble(value.toString());
                if (
                        v.getOperatorSchema() != null &&
                        v.getPropertyName().equals(key) &&
                        v.getWhetherStart().equals(InitiateStateEnum.start.getValue()) &&
                        maximum >= Value &&
                        mauimum <= Value
                ) {
                    result = true;
                }
            }
            //当值不在定义的范围时触发警报
            if (v.getRule().equals(DataTypeRuleEnum.OUTOFRANGE.getValue())) {
                String[] split = v.getOperatorSchema().split("\\|");
                double maximum = Double.parseDouble(split[1]);//最大
                double mauimum = Double.parseDouble(split[0]);//最小
                double Value = Double.parseDouble(value.toString());
                if (
                        v.getOperatorSchema() != null &&
                        v.getPropertyName().equals(key) &&
                        v.getWhetherStart().equals(InitiateStateEnum.start.getValue()) &&
                        maximum <= Value &&
                        mauimum >= Value
                ) {
                    result = true;
                }
            }
            //当属性值减去警报值大于警报限制（属性值-警报值）>= 警报限制 时 触发警报
            if (v.getRule().equals(DataTypeRuleEnum.DEVIATIONABOVE.getValue())){
                String[] split = v.getOperatorSchema().split("\\|");
                double alarmimum = Double.parseDouble(split[0]);//警报值
                double triggermum = Double.parseDouble(split[1]);//触发警报值
                double Value = Double.parseDouble(value.toString());
                if (
                        v.getOperatorSchema() != null &&
                        v.getPropertyName().equals(key) &&
                        v.getWhetherStart().equals(InitiateStateEnum.start.getValue()) &&
                        (Value - alarmimum) >= triggermum
                ) {
                    result = true;
                }
            }
            //当属性值减去警报值小于警报限制（属性值-警报值）<= 警报限制 时 触发警报
            if (v.getRule().equals(DataTypeRuleEnum.DEVIATIONBELOW.getValue())){
                String[] split = v.getOperatorSchema().split("\\|");
                double alarmimum = Double.parseDouble(split[0]);//警报值
                double triggermum = Double.parseDouble(split[1]);//触发警报值
                double Value = Double.parseDouble(value.toString());
                if (
                        v.getOperatorSchema() != null &&
                        v.getPropertyName().equals(key) &&
                        v.getWhetherStart().equals(InitiateStateEnum.start.getValue()) &&
                        (Value - alarmimum) <= triggermum
                ) {
                    result = true;
                }
            }
        }
        return result;
    }

}
