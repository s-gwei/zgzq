package org.jeecg.modules.system.config;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.system.entity.Thing;
import org.jeecg.modules.system.model.PropertyDefinition;
import org.jeecg.modules.system.model.ThingShape;
import org.jeecg.modules.system.service.IThingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 系统启动时加载物模型数据到redis
 * @author: boliu
 * @date: 2020年09月08日 11:34
 */
@Component
@Slf4j
public class ThingStartupRunner implements CommandLineRunner {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private IThingService thingService;

    @Override
    public void run(String... args) throws Exception {
        List<Thing> thingsList = thingService.list();
        int thingsListSize = thingsList.size();
        for (int i = 0; i < thingsListSize; i++) {
            Thing thing = thingsList.get(i);
            String thingName = thing.getName();
            String thingShape = thing.getThingShape();
            ThingShape thingShapeBean = JSONUtil.toBean(thingShape, ThingShape.class);
            Map<String, PropertyDefinition> propertyDefinitions = thingShapeBean.getPropertyDefinitions();
            //设备有属性时，将属性值写入缓存
            if (!propertyDefinitions.isEmpty()){
                Map<String, Object> propertyCacheMap = new HashMap<>();
                boolean isKeyExist = redisUtil.hasKey(thingName);
                //当redis中不存在设备属性时才写入缓存
                if (!isKeyExist){
                    propertyDefinitions.forEach((k,v)->{
                        propertyCacheMap.put(v.getName(),v.getValue());
                    });
                    redisUtil.hmset(thingName, propertyCacheMap);
                    //redisUtil.expire(thingName,86400);
                    log.info("成功加载数据到redis中>>>>>"+thingName+":"+propertyCacheMap);
                }else {
                    log.info("该缓存已存在！" + thingName);
                }
            }
        }
    }
}
