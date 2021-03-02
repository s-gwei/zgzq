package org.jeecg.modules.system.Incident;

import com.alibaba.fastjson.JSON;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.jeecg.modules.system.entity.Thing;
import org.jeecg.modules.system.entity.ThingTemplate;
import org.jeecg.modules.system.enumerate.DataTypeRuleEnum;
import org.jeecg.modules.system.mapper.ThingMapper;
import org.jeecg.modules.system.mapper.ThingTemplateMapper;
import org.jeecg.modules.system.model.EventDefinition;
import org.jeecg.modules.system.model.PropertyDefinition;
import org.jeecg.modules.system.model.Subscription;
import org.jeecg.modules.system.model.ThingShape;
import org.jeecg.modules.system.service.IncidentService;
import org.jeecg.modules.system.util.MyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhang ho jian
 * @date 2020/9/2
 * @time 9:12
 * @Description : 事件切面
 */

@Aspect //声明切面
@Component
public class IncidentThingAspect {

    /**
     *  上下文对象
     */
    @Resource
    private ApplicationContext applicationContext;
    @Resource
    private IncidentService incidentService;
    @Resource
    private ThingTemplateMapper thingTemplateMapper;


    /*
      * @Description:定义controller切入点拦截规则，拦截Incident注解的方法
      * @Author: zhj
      * @Date: 2020/9/2 9:20
      */
     @Pointcut("@annotation(org.jeecg.modules.system.Incident.IncidentThing)")
     public void IncidentThingAspect(){

     }


    /*
     * @Description:前置通知，切点之前执行
     * @Author: zhj
     * @Date: 2020/9/2 9:20
     */
    @Before("IncidentThingAspect()")
    public void deBefore(JoinPoint joinPoint) throws Throwable {

    }
    /*
     * @Description:切点执行成功之后执行
     * @Author: zhj
     * @Date: 2020/9/2 9:20
     */
    @AfterReturning(returning = "ret", pointcut = "IncidentThingAspect()")
    public void doAfterReturning(JoinPoint joinPoint,Object ret) throws Throwable {
        //获取目标方法的参数信息
        Object[] obj = joinPoint.getArgs();
        //查询模板
        ThingTemplate thingTemplate = thingTemplateMapper.selectById(obj[1].toString());
        //定义封装list
        List<Object> list =  new ArrayList<>();
        //转换成对应封装类
        for (Object argItem : obj) {
            if (argItem instanceof PropertyDefinition) {
                PropertyDefinition propertyDefinition = (PropertyDefinition) argItem;
                //获取扩展字段
                String thingShapeStr = thingTemplate.getThingShape();
                ThingShape thingShape = JSON.parseObject(thingShapeStr, ThingShape.class);
                Map<String, EventDefinition> eventDefinitions = thingShape.getEventDefinitions();
                Map<String, Subscription> subscriptions = thingShape.getSubscriptions();//订阅信息
                //遍历数据
                eventDefinitions.forEach(( k,v ) ->{
/*                    if (propertyDefinition.getName()!=null && v.getOperatorSchema()!=null && v.getPropertyName().equals(propertyDefinition.getName()) && v.getWhetherStart().equals("1") && v.getOperatorSchema().equals(propertyDefinition.getValue())){

                        incidentService.addWarnNews(eventDefinitions.get(v.getSerialNumber()),thing.getName());
                        applicationContext.publishEvent(new MyEvent(this,subscriptions,v.getSerialNumber()));
                    }*/

                    Boolean aBoolean = ruleMatching(propertyDefinition,k, v);
                    if (aBoolean){
                        //添加警报信息
                        incidentService.addWarnNews(eventDefinitions.get(v.getSerialNumber()),thingTemplate.getName());
                        applicationContext.publishEvent(new MyEvent(this,subscriptions,v.getSerialNumber()));
                    }
                });
            }
        }
    }
    /*
     * @Description:异常通知
     * @Author: zhj
     * @Date: 2020/9/2 9:20
     */
    @AfterThrowing(pointcut = "IncidentThingAspect()",throwing = "e")
    public void doAfterThrowable(Throwable e){

    }

    //匹配规则
    private Boolean  ruleMatching(PropertyDefinition propertyDefinition,String k ,EventDefinition v){
        Boolean result = false;
        if (v.getRule()!=null){
            //达到定义的值触发警报
            if (v.getRule().equals(DataTypeRuleEnum.EQUALTO.getValue())){
                if (propertyDefinition.getName() != null &&
                        v.getOperatorSchema() != null &&
                        v.getPropertyName().equals(propertyDefinition.getName()) &&
                        v.getWhetherStart().equals("1") &&
                        v.getOperatorSchema().equals(propertyDefinition.getValue())) {
                    result = true;
                }
            }
            //未达到定义的值触发警报
            if (v.getRule().equals(DataTypeRuleEnum.NOTEQUALTO.getValue())){
                if (propertyDefinition.getName() != null &&
                        v.getOperatorSchema() != null &&
                        v.getPropertyName().equals(propertyDefinition.getName()) &&
                        v.getWhetherStart().equals("1") &&
                        !v.getOperatorSchema().equals(propertyDefinition.getValue())) {
                    result = true;
                }
            }
            //超过或者达到定义的限制(上方)
            if (v.getRule().equals(DataTypeRuleEnum.ABOVE.getValue())) {
                int OperatorSchema = Integer.parseInt((String) v.getOperatorSchema());
                int Value = Integer.parseInt((String) propertyDefinition.getValue());
                if (propertyDefinition.getName() != null &&
                        v.getOperatorSchema() != null &&
                        v.getPropertyName().equals(propertyDefinition.getName()) &&
                        v.getWhetherStart().equals("1") &&
                        OperatorSchema <= Value) {
                    result = true;
                }
            }
            //低于或低于极限（下方）
            if (v.getRule().equals(DataTypeRuleEnum.UNDER.getValue())) {
                int OperatorSchema = Integer.parseInt((String) v.getOperatorSchema());
                int Value = Integer.parseInt((String) propertyDefinition.getValue());
                if (propertyDefinition.getName() != null &&
                        v.getOperatorSchema() != null &&
                        v.getPropertyName().equals(propertyDefinition.getName()) &&
                        v.getWhetherStart().equals("1") &&
                        OperatorSchema >= Value) {
                    result = true;
                }
            }
            //当值在定义的范围时触发警报
            if (v.getRule().equals(DataTypeRuleEnum.INRANGE.getValue())) {
                String[] split = v.getOperatorSchema().toString().split(",");
                int maximum = Integer.parseInt(split[1]);//最大
                int mauimum = Integer.parseInt(split[0]);//最小
                int Value = Integer.parseInt((String) propertyDefinition.getValue());
                if (propertyDefinition.getName() != null &&
                        v.getOperatorSchema() != null &&
                        v.getPropertyName().equals(propertyDefinition.getName()) &&
                        v.getWhetherStart().equals("1") &&
                        maximum >= Value &&
                        mauimum <= Value
                ) {
                    result = true;
                }
            }
            //当值不在定义的范围时触发警报
            if (v.getRule().equals(DataTypeRuleEnum.OUTOFRANGE.getValue())) {
                String[] split = v.getOperatorSchema().toString().split(",");
                int maximum = Integer.parseInt(split[1]);//最大
                int mauimum = Integer.parseInt(split[0]);//最小
                int Value = Integer.parseInt((String) propertyDefinition.getValue());
                if (propertyDefinition.getName() != null &&
                        v.getOperatorSchema() != null &&
                        v.getPropertyName().equals(propertyDefinition.getName()) &&
                        v.getWhetherStart().equals("1") &&
                        maximum <= Value &&
                        mauimum >= Value
                ) {
                    result = true;
                }
            }
            //当属性值减去警报值大于警报限制（属性值-警报值）>= 警报限制 时 触发警报
            if (v.getRule().equals(DataTypeRuleEnum.DEVIATIONABOVE.getValue())){
                String[] split = v.getOperatorSchema().toString().split(",");
                int alarmimum = Integer.parseInt(split[0]);//警报值
                int triggermum = Integer.parseInt(split[1]);//触发警报值
                int Value = Integer.parseInt((String) propertyDefinition.getValue());
                if (propertyDefinition.getName() != null &&
                        v.getOperatorSchema() != null &&
                        v.getPropertyName().equals(propertyDefinition.getName()) &&
                        v.getWhetherStart().equals("1") &&
                        (Value - alarmimum) >= triggermum
                ) {
                    result = true;
                }
            }
            //当属性值减去警报值小于警报限制（属性值-警报值）<= 警报限制 时 触发警报
            if (v.getRule().equals(DataTypeRuleEnum.DEVIATIONBELOW.getValue())){
                String[] split = v.getOperatorSchema().toString().split(",");
                int alarmimum = Integer.parseInt(split[0]);//警报值
                int triggermum = Integer.parseInt(split[1]);//触发警报值
                int Value = Integer.parseInt((String) propertyDefinition.getValue());
                if (propertyDefinition.getName() != null &&
                        v.getOperatorSchema() != null &&
                        v.getPropertyName().equals(propertyDefinition.getName()) &&
                        v.getWhetherStart().equals("1") &&
                        (Value - alarmimum) <= triggermum
                ) {
                    result = true;
                }
            }
        }
        return result;
    }


}
